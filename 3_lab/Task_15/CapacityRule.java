public class CapacityRule implements EnrollmentRule {
    @Override
    public RuleResult check(EnrollmentRequest r) {
        return r.course().enrolled() < r.course().capacity()
                ? new RuleResult.Pass()
                : new RuleResult.Defer("нет мест");
    }
}
