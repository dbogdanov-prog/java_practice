import java.util.List;
import java.util.Optional;

public class EnrollmentService {
    private final List<EnrollmentPolicy> policies;

    public EnrollmentService(List<EnrollmentPolicy> policies) {
        this.policies = List.copyOf(policies);
    }

    public Optional<String> check(Student student, Course course) {
        for (EnrollmentPolicy policy : policies) {
            Optional<String> reason = policy.reject(student, course);
            if (reason.isPresent()) return reason;
        }
        return Optional.empty();
    }
}
