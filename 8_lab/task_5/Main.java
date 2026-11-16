import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class Main {

    record Course(String code, String title, int duration, String subtitle) {
        Optional<String> subtitleValue() {
            return Optional.ofNullable(subtitle);
        }
    }

    static final Comparator<Course> LONGEST_THEN_SMALLEST_CODE = Comparator
            .comparingInt(Course::duration)
            .thenComparing(Course::code, Comparator.<String>reverseOrder());

    static Optional<Course> longest(List<Course> courses) {
        return courses.stream().max(LONGEST_THEN_SMALLEST_CODE);
    }

    static String longestTitleOrFallback(List<Course> courses, String fallback) {
        return longest(courses)
                .map(Course::title)
                .map(title -> title.toUpperCase())
                .orElse(fallback);
    }

    static String longestSubtitleOrFallback(List<Course> courses, String fallback) {
        return longest(courses)
                .flatMap(Course::subtitleValue)
                .map(String::strip)
                .filter(text -> !text.isEmpty())
                .orElseGet(() -> fallback);
    }

    static Course requireLongest(List<Course> courses) {
        return longest(courses)
                .orElseThrow(() -> new NoSuchElementException("catalog contains no courses"));
    }

    static List<Course> data() {
        return List.of(
                new Course("JV-101", "Java Basics", 40, "From zero to first program"),
                new Course("JV-301", "Java Concurrency", 60, null),
                new Course("JV-310", "JVM Internals", 60, "  "),
                new Course("AR-340", "Software Architecture", 72, "Patterns and trade-offs"),
                new Course("AR-341", "Software Architecture Lab", 72, null));
    }

    public static void main(String[] args) {
        List<Course> courses = data();
        List<Course> empty = List.of();

        System.out.println("Longest course          : " + longest(courses));
        System.out.println("Longest course (empty)  : " + longest(empty));
        System.out.println("Title upper-cased       : " + longestTitleOrFallback(courses, "NO COURSES"));
        System.out.println("Title for empty catalog : " + longestTitleOrFallback(empty, "NO COURSES"));
        System.out.println("Subtitle of longest     : " + longestSubtitleOrFallback(courses, "no subtitle"));
        System.out.println("Subtitle for empty list : " + longestSubtitleOrFallback(empty, "no subtitle"));

        List<Course> noSubtitle = courses.stream().filter(c -> c.subtitle() == null || c.subtitle().isBlank()).toList();
        System.out.println("Subtitle when missing   : " + longestSubtitleOrFallback(noSubtitle, "no subtitle"));

        longest(courses).ifPresentOrElse(
                course -> System.out.println("ifPresentOrElse         : " + course.code() + " " + course.duration() + "h"),
                () -> System.out.println("ifPresentOrElse         : nothing to show"));

        System.out.println("requireLongest          : " + requireLongest(courses).code());
        try {
            requireLongest(empty);
        } catch (NoSuchElementException e) {
            System.out.println("requireLongest (empty)  : " + e.getMessage());
        }

        boolean fieldsHaveNoOptional = true;
        for (var component : Course.class.getRecordComponents()) {
            fieldsHaveNoOptional &= !Optional.class.isAssignableFrom(component.getType());
        }
        System.out.println("Course stores no Optional field: " + fieldsHaveNoOptional);
    }
}
