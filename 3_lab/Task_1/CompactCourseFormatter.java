public class CompactCourseFormatter implements CourseFormatter {
    @Override
    public String format(Course course) {
        return course.title() + " (" + course.hours() + " ч)";
    }
}
