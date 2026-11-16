import java.util.Locale;

public class JsonCourseFormatter implements CourseFormatter {
    @Override
    public String format(Course course) {
        return String.format(Locale.US, "{\"title\":\"%s\",\"hours\":%d,\"price\":%.2f}",
                course.title(), course.hours(), course.price());
    }
}
