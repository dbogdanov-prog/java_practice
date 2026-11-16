public class EnrollmentService {
    private final EnrollmentRule rule;

    public EnrollmentService(EnrollmentRule rule) { this.rule = rule; }

    public EnrollmentDecision enroll(Student student, Course course) {
        String name = student.name();
        return switch (rule.check(new EnrollmentRequest(student, course))) {
            case RuleResult.Pass p -> new EnrollmentDecision.Accepted(name);
            case RuleResult.Fail f -> new EnrollmentDecision.Rejected(name, f.reason());
            case RuleResult.Defer d -> new EnrollmentDecision.WaitListed(name, d.reason());
        };
    }
}
