import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Main {

    enum Level { BEGINNER, INTERMEDIATE, ADVANCED }

    record Course(String code, Level level, int duration) {}

    record LevelSummary(long count, long totalHours, double averageHours) {}

    record Summary(Map<Level, LevelSummary> byLevel, long totalCourses, long totalHours, double overallAverage) {
        Summary {
            byLevel = Collections.unmodifiableMap(new EnumMap<>(byLevel));
        }

        static Summary from(Map<Level, LevelSummary> byLevel) {
            long courses = byLevel.values().stream().mapToLong(LevelSummary::count).sum();
            long hours = byLevel.values().stream().mapToLong(LevelSummary::totalHours).sum();
            double average = courses == 0 ? 0.0 : (double) hours / courses;
            return new Summary(byLevel, courses, hours, average);
        }
    }

    static Summary singlePass(List<Course> courses, AtomicLong visited) {
        return courses.stream()
                .peek(course -> visited.incrementAndGet())
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(
                                Course::level,
                                () -> new EnumMap<>(Level.class),
                                Collectors.collectingAndThen(
                                        Collectors.summarizingInt(Course::duration),
                                        stats -> new LevelSummary(stats.getCount(), stats.getSum(), stats.getAverage()))),
                        Summary::from));
    }

    static Summary threePasses(List<Course> courses, AtomicLong visited) {
        Map<Level, Long> counts = courses.stream()
                .peek(course -> visited.incrementAndGet())
                .collect(Collectors.groupingBy(Course::level, TreeMap::new, Collectors.counting()));
        Map<Level, Integer> totals = courses.stream()
                .peek(course -> visited.incrementAndGet())
                .collect(Collectors.groupingBy(Course::level, TreeMap::new, Collectors.summingInt(Course::duration)));
        Map<Level, Double> averages = courses.stream()
                .peek(course -> visited.incrementAndGet())
                .collect(Collectors.groupingBy(Course::level, TreeMap::new, Collectors.averagingInt(Course::duration)));
        Map<Level, LevelSummary> byLevel = new EnumMap<>(Level.class);
        counts.forEach((level, count) -> byLevel.put(level, new LevelSummary(count, totals.get(level), averages.get(level))));
        return Summary.from(byLevel);
    }

    static List<Course> data() {
        return List.of(
                new Course("JV-101", Level.BEGINNER, 40),
                new Course("PY-101", Level.BEGINNER, 40),
                new Course("GT-100", Level.BEGINNER, 12),
                new Course("SQ-110", Level.BEGINNER, 24),
                new Course("JV-201", Level.INTERMEDIATE, 36),
                new Course("JV-210", Level.INTERMEDIATE, 36),
                new Course("SP-220", Level.INTERMEDIATE, 48),
                new Course("DB-230", Level.INTERMEDIATE, 30),
                new Course("JV-301", Level.ADVANCED, 60),
                new Course("JV-310", Level.ADVANCED, 60),
                new Course("AR-340", Level.ADVANCED, 72));
    }

    public static void main(String[] args) {
        List<Course> courses = new ArrayList<>(data());

        AtomicLong visitedOnce = new AtomicLong();
        Summary summary = singlePass(courses, visitedOnce);

        AtomicLong visitedThrice = new AtomicLong();
        Summary naive = threePasses(courses, visitedThrice);

        summary.byLevel().forEach((level, s) -> System.out.printf(Locale.ROOT,
                "%-13s count=%d total=%d average=%.2f%n", level, s.count(), s.totalHours(), s.averageHours()));
        System.out.printf(Locale.ROOT, "TOTAL         count=%d total=%d average=%.2f%n",
                summary.totalCourses(), summary.totalHours(), summary.overallAverage());

        System.out.println();
        System.out.println("Elements visited, single pass : " + visitedOnce.get());
        System.out.println("Elements visited, three passes: " + visitedThrice.get());
        System.out.println("Both approaches give equal summary: " + summary.equals(naive));

        double averageOfAverages = summary.byLevel().values().stream()
                .mapToDouble(LevelSummary::averageHours).average().orElse(0.0);
        System.out.printf(Locale.ROOT, "Overall average (hours / courses): %.4f%n", summary.overallAverage());
        System.out.printf(Locale.ROOT, "Average of level averages (wrong) : %.4f%n", averageOfAverages);

        try {
            summary.byLevel().put(Level.BEGINNER, new LevelSummary(0, 0, 0));
        } catch (UnsupportedOperationException e) {
            System.out.println("Summary is immutable: UnsupportedOperationException");
        }
    }
}
