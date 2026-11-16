import java.util.Locale;

public class DetailedCourseFormatter implements CourseFormatter {
    @Override
    public String format(Course course) {
        return String.format(Locale.US, "Курс: %s%nЧасов: %d%nЦена: %.2f",
                course.title(), course.hours(), course.price());
    }
}
