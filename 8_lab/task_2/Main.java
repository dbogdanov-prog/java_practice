import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Main {

    record Module(String title, List<String> topics) {}

    record Course(String code, String title, List<Module> modules) {}

    record TopicLink(String topic, String courseCode) {}

    static String normalize(String raw) {
        return Normalizer.normalize(raw, Normalizer.Form.NFKC)
                .strip()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\s\\p{Pd}_]+", " ");
    }

    static Set<String> uniqueTopics(List<Course> courses) {
        return courses.stream()
                .flatMap(course -> course.modules().stream())
                .flatMap(module -> module.topics().stream())
                .map(Main::normalize)
                .filter(topic -> !topic.isEmpty())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    static Map<String, Set<String>> topicIndex(List<Course> courses) {
        return courses.stream()
                .flatMap(course -> course.modules().stream()
                        .flatMap(module -> module.topics().stream())
                        .map(Main::normalize)
                        .filter(topic -> !topic.isEmpty())
                        .map(topic -> new TopicLink(topic, course.code())))
                .collect(Collectors.groupingBy(
                        TopicLink::topic,
                        TreeMap::new,
                        Collectors.mapping(
                                TopicLink::courseCode,
                                Collectors.<String, Set<String>>toCollection(TreeSet::new))));
    }

    static List<Course> data() {
        return List.of(
                new Course("JV-101", "Java Basics", List.of(
                        new Module("Syntax", List.of("Variables", "  Control Flow ", "Methods")),
                        new Module("OOP", List.of("Classes", "Inheritance", "interfaces")))),
                new Course("JV-201", "Java Collections", List.of(
                        new Module("Lists and Sets", List.of("Collections", "generics", "Control   flow")),
                        new Module("Maps", List.of("HashMap", "Hash-Map", "TreeMap")))),
                new Course("JV-210", "Java Streams", List.of(
                        new Module("Pipeline", List.of("Lambdas", "Stream API", "STREAM  API")),
                        new Module("Collectors", List.of("Grouping", "Generics", "  ")))),
                new Course("SP-220", "Spring Core", List.of(
                        new Module("Container", List.of("Dependency Injection", "Beans", "Interfaces")),
                        new Module("Empty module", List.of()))),
                new Course("DB-230", "Database Design", List.of(
                        new Module("Relational", List.of("SQL", "Normalization", "Indexes")))),
                new Course("OT-000", "Orientation", List.of()));
    }

    public static void main(String[] args) {
        List<Course> courses = data();

        Set<String> topics = uniqueTopics(courses);
        System.out.println("Unique normalized topics (" + topics.size() + "):");
        topics.forEach(topic -> System.out.println("  " + topic));

        Map<String, Set<String>> index = topicIndex(courses);
        System.out.println();
        System.out.println("Reverse index topic -> courses:");
        index.forEach((topic, codes) -> System.out.printf("  %-22s %s%n", topic, codes));

        System.out.println();
        System.out.println("Index keys equal unique topics: " + index.keySet().equals(topics));

        boolean stable = true;
        for (int seed = 0; seed < 200; seed++) {
            List<Course> shuffled = new ArrayList<>(courses);
            Collections.shuffle(shuffled, new Random(seed));
            stable &= uniqueTopics(shuffled).equals(topics) && topicIndex(shuffled).equals(index);
        }
        System.out.println("Same result for 200 shuffled inputs: " + stable);
    }
}
