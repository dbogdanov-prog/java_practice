public class AgeRule implements EnrollmentRule {
    @Override
    public RuleResult check(EnrollmentRequest r) {
        return r.student().age() >= r.course().minAge()
                ? new RuleResult.Pass()
                : new RuleResult.Fail("возраст меньше " + r.course().minAge());
    }
}
