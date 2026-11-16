public class NotBlockedRule implements EnrollmentRule {
    @Override
    public RuleResult check(EnrollmentRequest r) {
        return r.student().blocked() ? new RuleResult.Fail("студент заблокирован") : new RuleResult.Pass();
    }
}
