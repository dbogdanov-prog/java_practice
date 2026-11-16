import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Main {

    enum Level { BEGINNER, INTERMEDIATE, ADVANCED }

    record Course(String code, String title, Level level, int duration) {}

    static final Comparator<Course> LEVEL_ONLY = Comparator
            .comparing(Course::level);

    static final Comparator<Course> WITHOUT_FINAL_TIE_BREAKER = Comparator
            .comparing(Course::level)
            .thenComparing(Course::duration, Comparator.<Integer>reverseOrder())
            .thenComparing(Course::code);

    static final Comparator<Course> DETERMINISTIC = WITHOUT_FINAL_TIE_BREAKER
            .thenComparing(Course::title);

    static List<Course> data() {
        return List.of(
                new Course("JV-101", "Java Basics", Level.BEGINNER, 40),
                new Course("PY-101", "Python Basics", Level.BEGINNER, 40),
                new Course("GT-100", "Git Essentials", Level.BEGINNER, 12),
                new Course("SQ-110", "SQL Starter", Level.BEGINNER, 24),
                new Course("JV-201", "Java Collections", Level.INTERMEDIATE, 36),
                new Course("JV-210", "Java Streams", Level.INTERMEDIATE, 36),
                new Course("SP-220", "Spring Core", Level.INTERMEDIATE, 48),
                new Course("DB-230", "Database Design", Level.INTERMEDIATE, 30),
                new Course("JV-301", "Java Concurrency", Level.ADVANCED, 60),
                new Course("JV-301", "Java Concurrency Lab", Level.ADVANCED, 60),
                new Course("JV-310", "JVM Internals", Level.ADVANCED, 60),
                new Course("AR-340", "Software Architecture", Level.ADVANCED, 72));
    }

    static List<Course> sorted(List<Course> source, Comparator<Course> order) {
        return source.stream().sorted(order).toList();
    }

    static boolean consistentWithEquals(List<Course> source, Comparator<Course> order) {
        for (Course a : source) {
            for (Course b : source) {
                if ((order.compare(a, b) == 0) != a.equals(b)) {
                    return false;
                }
            }
        }
        return true;
    }

    static int distinctResults(List<Course> base, Comparator<Course> order, int trials) {
        Set<List<Course>> results = new HashSet<>();
        for (int seed = 0; seed < trials; seed++) {
            List<Course> shuffled = new ArrayList<>(base);
            Collections.shuffle(shuffled, new Random(seed));
            results.add(sorted(shuffled, order));
        }
        return results.size();
    }

    static void print(List<Course> courses) {
        for (Course c : courses) {
            System.out.printf("%-8s %-24s %-13s %3d%n", c.code(), c.title(), c.level(), c.duration());
        }
    }

    public static void main(String[] args) {
        List<Course> base = data();
        int trials = 1000;

        System.out.println("Sorted by level asc, duration desc, code asc, title asc:");
        print(sorted(base, DETERMINISTIC));

        System.out.println();
        System.out.println("Distinct results over " + trials + " random input orders:");
        System.out.println("level only                       : " + distinctResults(base, LEVEL_ONLY, trials));
        System.out.println("level, duration, code            : " + distinctResults(base, WITHOUT_FINAL_TIE_BREAKER, trials));
        System.out.println("level, duration, code, title     : " + distinctResults(base, DETERMINISTIC, trials));

        System.out.println();
        System.out.println("Comparator consistent with equals (level only)  : " + consistentWithEquals(base, LEVEL_ONLY));
        System.out.println("Comparator consistent with equals (deterministic): " + consistentWithEquals(base, DETERMINISTIC));

        List<Course> reversed = new ArrayList<>(base);
        Collections.reverse(reversed);
        boolean same = sorted(base, DETERMINISTIC).equals(sorted(reversed, DETERMINISTIC));
        System.out.println("Original order and reversed order give same result: " + same);
    }
}
