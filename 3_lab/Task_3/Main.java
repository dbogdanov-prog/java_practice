public class Main {
    public static void main(String[] args) {
        Course course = new Course("Java Basics", 40, 199.5);

        CourseFormatter[] formatters = {
                new CompactCourseFormatter(),
                new DetailedCourseFormatter(),
                new JsonCourseFormatter()
        };

        for (CourseFormatter formatter : formatters) {
            System.out.println(formatter.format(course));
            System.out.println("---");
        }
    }
}
