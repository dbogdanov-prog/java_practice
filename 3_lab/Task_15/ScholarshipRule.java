public class ScholarshipRule implements EnrollmentRule {
    @Override
    public RuleResult check(EnrollmentRequest r) {
        return r.student().scholarship() ? new RuleResult.Pass() : new RuleResult.Fail("нет стипендии");
    }
}
