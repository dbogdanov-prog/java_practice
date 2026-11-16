import java.util.Optional;

public class PrerequisitePolicy implements EnrollmentPolicy {
    @Override
    public Optional<String> reject(Student student, Course course) {
        return student.completed().containsAll(course.prerequisites())
                ? Optional.empty()
                : Optional.of("Не пройдены prerequisites " + course.prerequisites());
    }
}
