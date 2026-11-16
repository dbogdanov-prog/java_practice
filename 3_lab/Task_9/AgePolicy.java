import java.util.Optional;

public class AgePolicy implements EnrollmentPolicy {
    @Override
    public Optional<String> reject(Student student, Course course) {
        return student.age() >= course.minAge()
                ? Optional.empty()
                : Optional.of("Возраст меньше " + course.minAge());
    }
}
