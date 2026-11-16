public class Main {
    public static void main(String[] args) {
        Course course = new Course("Java Basics", 40, 199.5);

        CourseFormatter formatter = new CompactCourseFormatter();
        System.out.println(formatter.format(course));

        formatter = new DetailedCourseFormatter();
        System.out.println(formatter.format(course));
    }
}
