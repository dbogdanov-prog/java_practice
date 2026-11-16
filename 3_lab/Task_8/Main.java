public class Main {
    static void importDefaults(CourseWriter writer) {
        writer.save(new Course(1, "Java"));
        writer.save(new Course(2, "SQL"));
    }

    static void printAll(CourseReader reader) {
        reader.findAll().forEach(System.out::println);
    }

    public static void main(String[] args) {
        OldCourseRepository old = new OldReadOnlyCourseRepository();
        try {
            old.save(new Course(1, "Java"));
        } catch (UnsupportedOperationException e) {
            System.out.println("Старый дизайн: save упал: " + e.getMessage());
        }

        InMemoryCourseRepository repo = new InMemoryCourseRepository();
        importDefaults(repo);
        printAll(repo);

        ReadOnlyCourseRepository readOnly = new ReadOnlyCourseRepository(repo);
        printAll(readOnly);
        // importDefaults(readOnly);  // не компилируется: ReadOnlyCourseRepository не CourseWriter
    }
}
