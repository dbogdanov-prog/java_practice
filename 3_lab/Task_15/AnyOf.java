import java.util.List;

public class AnyOf extends CompositeRule {
    public AnyOf(EnrollmentRule... rules) { super(rules); }

    @Override
    public RuleResult check(EnrollmentRequest request) {
        List<RuleResult> results = results(request);
        if (has(results, RuleResult.Pass.class)) return new RuleResult.Pass();
        if (has(results, RuleResult.Defer.class)) return new RuleResult.Defer(reasons(results, false));
        return new RuleResult.Fail(reasons(results, true));
    }
}
