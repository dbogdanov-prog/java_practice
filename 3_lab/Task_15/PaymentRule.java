public class PaymentRule implements EnrollmentRule {
    @Override
    public RuleResult check(EnrollmentRequest r) {
        return r.student().paid() ? new RuleResult.Pass() : new RuleResult.Fail("нет оплаты");
    }
}
