import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collector;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class Main {

    enum Level { BEGINNER, INTERMEDIATE, ADVANCED }

    enum EventType { ENROLLED, COMPLETED }

    enum AnomalyKind {
        BLANK_STUDENT,
        UNKNOWN_COURSE,
        DUPLICATE_ENROLLMENT,
        COMPLETION_WITHOUT_ENROLLMENT,
        DUPLICATE_COMPLETION
    }

    record Event(String studentId, String courseCode, EventType type) {}

    record Pair(String studentId, String courseCode) {}

    record Anomaly(AnomalyKind kind, String studentId, String courseCode) implements Comparable<Anomaly> {
        static final Comparator<Anomaly> ORDER = Comparator
                .comparing(Anomaly::kind)
                .thenComparing(Anomaly::studentId)
                .thenComparing(Anomaly::courseCode);

        @Override
        public int compareTo(Anomaly other) {
            return ORDER.compare(this, other);
        }
    }

    record CourseRate(String courseCode, long enrolled, long completed, double completionRate) {}

    record Report(
            long events,
            int uniqueStudents,
            Map<String, CourseRate> byCourse,
            List<CourseRate> top3,
            Map<Level, Long> levelDistribution,
            Map<AnomalyKind, Long> anomalyCounts,
            List<Anomaly> anomalySample) {

        Report {
            byCourse = Collections.unmodifiableMap(new TreeMap<>(byCourse));
            top3 = List.copyOf(top3);
            levelDistribution = Collections.unmodifiableMap(new TreeMap<>(levelDistribution));
            anomalyCounts = Collections.unmodifiableMap(new TreeMap<>(anomalyCounts));
            anomalySample = List.copyOf(anomalySample);
        }
    }

    static final int SAMPLE_LIMIT = 20;

    static final Map<String, Level> CATALOG = Map.ofEntries(
            Map.entry("GT-100", Level.BEGINNER),
            Map.entry("JV-101", Level.BEGINNER),
            Map.entry("PY-101", Level.BEGINNER),
            Map.entry("SQ-110", Level.BEGINNER),
            Map.entry("DB-230", Level.INTERMEDIATE),
            Map.entry("JV-201", Level.INTERMEDIATE),
            Map.entry("JV-210", Level.INTERMEDIATE),
            Map.entry("SP-220", Level.INTERMEDIATE),
            Map.entry("AR-340", Level.ADVANCED),
            Map.entry("JV-301", Level.ADVANCED),
            Map.entry("JV-310", Level.ADVANCED));

    static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    static Comparator<CourseRate> topOrder() {
        return Comparator
                .comparingLong(CourseRate::enrolled).reversed()
                .thenComparing(Comparator.comparingDouble(CourseRate::completionRate).reversed())
                .thenComparing(CourseRate::courseCode);
    }

    static final class Accumulator {
        private long events;
        private final Set<String> students = new HashSet<>();
        private final Map<Pair, int[]> pairs = new HashMap<>();
        private final Map<AnomalyKind, Long> eventAnomalies = new EnumMap<>(AnomalyKind.class);
        private final TreeSet<Anomaly> sample = new TreeSet<>();

        void accept(Event event) {
            events++;
            if (blank(event.studentId())) {
                flag(new Anomaly(AnomalyKind.BLANK_STUDENT, "", String.valueOf(event.courseCode())));
                return;
            }
            students.add(event.studentId());
            if (!CATALOG.containsKey(event.courseCode())) {
                flag(new Anomaly(AnomalyKind.UNKNOWN_COURSE, event.studentId(), String.valueOf(event.courseCode())));
                return;
            }
            int[] counters = pairs.computeIfAbsent(new Pair(event.studentId(), event.courseCode()), key -> new int[2]);
            counters[event.type().ordinal()]++;
        }

        private void flag(Anomaly anomaly) {
            eventAnomalies.merge(anomaly.kind(), 1L, Long::sum);
            remember(sample, anomaly);
        }

        private static void remember(TreeSet<Anomaly> target, Anomaly anomaly) {
            target.add(anomaly);
            if (target.size() > SAMPLE_LIMIT) {
                target.pollLast();
            }
        }

        Accumulator merge(Accumulator other) {
            events += other.events;
            students.addAll(other.students);
            other.pairs.forEach((pair, counters) -> pairs.merge(pair, counters, (a, b) -> {
                a[0] += b[0];
                a[1] += b[1];
                return a;
            }));
            other.eventAnomalies.forEach((kind, count) -> eventAnomalies.merge(kind, count, Long::sum));
            other.sample.forEach(anomaly -> remember(sample, anomaly));
            return this;
        }

        Report finish() {
            Map<String, long[]> perCourse = new TreeMap<>();
            CATALOG.keySet().forEach(code -> perCourse.put(code, new long[2]));
            Map<Level, Long> levels = new EnumMap<>(Level.class);
            Map<AnomalyKind, Long> anomalyCounts = new EnumMap<>(AnomalyKind.class);
            anomalyCounts.putAll(eventAnomalies);
            TreeSet<Anomaly> finalSample = new TreeSet<>(sample);

            for (Map.Entry<Pair, int[]> entry : pairs.entrySet()) {
                Pair pair = entry.getKey();
                int enrolled = entry.getValue()[0];
                int completed = entry.getValue()[1];
                if (enrolled > 0) {
                    long[] totals = perCourse.get(pair.courseCode());
                    totals[0]++;
                    if (completed > 0) {
                        totals[1]++;
                    }
                    levels.merge(CATALOG.get(pair.courseCode()), 1L, Long::sum);
                }
                if (enrolled > 1) {
                    anomalyCounts.merge(AnomalyKind.DUPLICATE_ENROLLMENT, 1L, Long::sum);
                    remember(finalSample, new Anomaly(AnomalyKind.DUPLICATE_ENROLLMENT, pair.studentId(), pair.courseCode()));
                }
                if (completed > 0 && enrolled == 0) {
                    anomalyCounts.merge(AnomalyKind.COMPLETION_WITHOUT_ENROLLMENT, 1L, Long::sum);
                    remember(finalSample, new Anomaly(AnomalyKind.COMPLETION_WITHOUT_ENROLLMENT, pair.studentId(), pair.courseCode()));
                }
                if (completed > 1) {
                    anomalyCounts.merge(AnomalyKind.DUPLICATE_COMPLETION, 1L, Long::sum);
                    remember(finalSample, new Anomaly(AnomalyKind.DUPLICATE_COMPLETION, pair.studentId(), pair.courseCode()));
                }
            }

            Map<String, CourseRate> rates = new TreeMap<>();
            perCourse.forEach((code, totals) -> rates.put(code, new CourseRate(
                    code, totals[0], totals[1], totals[0] == 0 ? 0.0 : (double) totals[1] / totals[0])));

            List<CourseRate> top3 = rates.values().stream()
                    .filter(rate -> rate.enrolled() > 0)
                    .sorted(topOrder())
                    .limit(3)
                    .toList();

            return new Report(events, students.size(), rates, top3, levels, anomalyCounts, new ArrayList<>(finalSample));
        }
    }

    static Collector<Event, Accumulator, Report> reportCollector() {
        return Collector.of(
                Accumulator::new,
                Accumulator::accept,
                Accumulator::merge,
                Accumulator::finish,
                Collector.Characteristics.UNORDERED);
    }

    static Report streamReport(Stream<Event> events) {
        return events.collect(reportCollector());
    }

    static Report loopReport(Iterator<Event> events) {
        long total = 0;
        Set<String> students = new HashSet<>();
        Map<String, Set<String>> enrolled = new HashMap<>();
        Map<String, Set<String>> completedPairs = new HashMap<>();
        Map<Pair, Integer> enrollCounts = new HashMap<>();
        Map<Pair, Integer> completeCounts = new HashMap<>();
        List<Anomaly> anomalies = new ArrayList<>();
        Map<AnomalyKind, Long> counts = new EnumMap<>(AnomalyKind.class);

        while (events.hasNext()) {
            Event event = events.next();
            total++;
            if (blank(event.studentId())) {
                anomalies.add(new Anomaly(AnomalyKind.BLANK_STUDENT, "", String.valueOf(event.courseCode())));
                counts.merge(AnomalyKind.BLANK_STUDENT, 1L, Long::sum);
                continue;
            }
            students.add(event.studentId());
            if (!CATALOG.containsKey(event.courseCode())) {
                anomalies.add(new Anomaly(AnomalyKind.UNKNOWN_COURSE, event.studentId(), event.courseCode()));
                counts.merge(AnomalyKind.UNKNOWN_COURSE, 1L, Long::sum);
                continue;
            }
            Pair pair = new Pair(event.studentId(), event.courseCode());
            if (event.type() == EventType.ENROLLED) {
                enrollCounts.merge(pair, 1, Integer::sum);
            } else {
                completeCounts.merge(pair, 1, Integer::sum);
            }
        }

        Set<Pair> allPairs = new HashSet<>(enrollCounts.keySet());
        allPairs.addAll(completeCounts.keySet());
        Map<String, long[]> perCourse = new TreeMap<>();
        CATALOG.keySet().forEach(code -> perCourse.put(code, new long[2]));
        Map<Level, Long> levels = new EnumMap<>(Level.class);

        for (Pair pair : allPairs) {
            int enrolledTimes = enrollCounts.getOrDefault(pair, 0);
            int completedTimes = completeCounts.getOrDefault(pair, 0);
            if (enrolledTimes > 0) {
                perCourse.get(pair.courseCode())[0]++;
                if (completedTimes > 0) {
                    perCourse.get(pair.courseCode())[1]++;
                }
                levels.merge(CATALOG.get(pair.courseCode()), 1L, Long::sum);
            }
            if (enrolledTimes > 1) {
                anomalies.add(new Anomaly(AnomalyKind.DUPLICATE_ENROLLMENT, pair.studentId(), pair.courseCode()));
                counts.merge(AnomalyKind.DUPLICATE_ENROLLMENT, 1L, Long::sum);
            }
            if (completedTimes > 0 && enrolledTimes == 0) {
                anomalies.add(new Anomaly(AnomalyKind.COMPLETION_WITHOUT_ENROLLMENT, pair.studentId(), pair.courseCode()));
                counts.merge(AnomalyKind.COMPLETION_WITHOUT_ENROLLMENT, 1L, Long::sum);
            }
            if (completedTimes > 1) {
                anomalies.add(new Anomaly(AnomalyKind.DUPLICATE_COMPLETION, pair.studentId(), pair.courseCode()));
                counts.merge(AnomalyKind.DUPLICATE_COMPLETION, 1L, Long::sum);
            }
        }

        Map<String, CourseRate> rates = new TreeMap<>();
        for (Map.Entry<String, long[]> entry : perCourse.entrySet()) {
            long[] totals = entry.getValue();
            rates.put(entry.getKey(), new CourseRate(entry.getKey(), totals[0], totals[1],
                    totals[0] == 0 ? 0.0 : (double) totals[1] / totals[0]));
        }

        List<CourseRate> ranked = new ArrayList<>();
        for (CourseRate rate : rates.values()) {
            if (rate.enrolled() > 0) {
                ranked.add(rate);
            }
        }
        ranked.sort(topOrder());
        List<CourseRate> top3 = ranked.subList(0, Math.min(3, ranked.size()));

        Collections.sort(anomalies);
        List<Anomaly> sample = new ArrayList<>(new TreeSet<>(anomalies));
        sample = sample.subList(0, Math.min(SAMPLE_LIMIT, sample.size()));

        return new Report(total, students.size(), rates, top3, levels, counts, sample);
    }

    static long mix(long value) {
        long z = value + 0x9E3779B97F4A7C15L;
        z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
        z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
        return z ^ (z >>> 31);
    }

    static final List<String> CODES = List.copyOf(new TreeSet<>(CATALOG.keySet()));

    static Event event(long index) {
        long pairSeed = mix(index >> 1);
        long noise = Math.floorMod(mix(index * 7 + 13), 100_000L);
        long studentNumber = Math.floorMod(pairSeed, 5_000_000L);
        String student = String.format(Locale.ROOT, "S%07d", studentNumber);
        String course = CODES.get((int) Math.floorMod(pairSeed >>> 20, (long) CODES.size()));
        EventType type = EventType.ENROLLED;

        if ((index & 1) == 1) {
            if (noise < 35_000) {
                type = EventType.COMPLETED;
            } else {
                long other = mix(index + (1L << 40));
                student = String.format(Locale.ROOT, "S%07d", Math.floorMod(other, 5_000_000L));
                course = CODES.get((int) Math.floorMod(other >>> 20, (long) CODES.size()));
            }
        } else if (noise < 400) {
            type = EventType.COMPLETED;
        }

        if (noise >= 99_950) {
            student = noise % 2 == 0 ? "" : student;
        }
        if (noise >= 99_900 && noise < 99_950) {
            course = "ZZ-999";
        }
        return new Event(student, course, type);
    }

    static Stream<Event> source(long size) {
        return LongStream.range(0, size).mapToObj(Main::event);
    }

    static void print(Report report) {
        System.out.println("Events processed : " + report.events());
        System.out.println("Unique students  : " + report.uniqueStudents());
        System.out.println();
        System.out.println("Completion rate by course:");
        report.byCourse().values().forEach(rate -> System.out.printf(Locale.ROOT,
                "  %-7s enrolled=%-6d completed=%-6d rate=%.4f%n",
                rate.courseCode(), rate.enrolled(), rate.completed(), rate.completionRate()));
        System.out.println();
        System.out.println("Top-3 courses by enrolled students:");
        report.top3().forEach(rate -> System.out.printf(Locale.ROOT,
                "  %-7s enrolled=%d rate=%.4f%n", rate.courseCode(), rate.enrolled(), rate.completionRate()));
        System.out.println();
        System.out.println("Enrollments by level: " + report.levelDistribution());
        System.out.println("Anomaly counts      : " + report.anomalyCounts());
        System.out.println("Anomaly sample (" + report.anomalySample().size() + " of at most " + SAMPLE_LIMIT + "):");
        report.anomalySample().stream().limit(8).forEach(a -> System.out.println("  " + a));
    }

    public static void main(String[] args) {
        long size = 1_000_000;

        Report report = streamReport(source(size));
        print(report);

        System.out.println();
        Report parallel = streamReport(source(size).parallel());
        System.out.println("Parallel stream gives identical report : " + report.equals(parallel));

        Report loop = loopReport(source(size).iterator());
        System.out.println("Plain loop gives identical report      : " + report.equals(loop));

        Stream<Event> once = source(10);
        once.count();
        try {
            once.count();
        } catch (IllegalStateException e) {
            System.out.println("Second traversal of the same stream    : IllegalStateException");
        }

        Report empty = streamReport(Stream.empty());
        System.out.println("Empty input: events=" + empty.events() + " students=" + empty.uniqueStudents()
                + " top3=" + empty.top3() + " anomalies=" + empty.anomalyCounts());

        try {
            report.top3().add(null);
        } catch (UnsupportedOperationException e) {
            System.out.println("Report collections are immutable       : UnsupportedOperationException");
        }
    }
}
