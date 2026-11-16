import java.util.Optional;

public class CapacityPolicy implements EnrollmentPolicy {
    @Override
    public Optional<String> reject(Student student, Course course) {
        return course.enrolled() < course.capacity()
                ? Optional.empty()
                : Optional.of("Нет мест");
    }
}
