public class PrerequisiteRule implements EnrollmentRule {
    @Override
    public RuleResult check(EnrollmentRequest r) {
        return r.student().completed().containsAll(r.course().prerequisites())
                ? new RuleResult.Pass()
                : new RuleResult.Fail("не пройдены " + r.course().prerequisites());
    }
}
