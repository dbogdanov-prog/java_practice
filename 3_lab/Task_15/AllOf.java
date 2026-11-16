import java.util.List;

public class AllOf extends CompositeRule {
    public AllOf(EnrollmentRule... rules) { super(rules); }

    @Override
    public RuleResult check(EnrollmentRequest request) {
        List<RuleResult> results = results(request);
        if (has(results, RuleResult.Fail.class)) return new RuleResult.Fail(reasons(results, true));
        if (has(results, RuleResult.Defer.class)) return new RuleResult.Defer(reasons(results, false));
        return new RuleResult.Pass();
    }
}
