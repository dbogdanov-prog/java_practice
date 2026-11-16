import java.util.ArrayList;
import java.util.List;

// общая часть AllOf и AnyOf: список правил и сбор результатов
public abstract class CompositeRule implements EnrollmentRule {
    protected final List<EnrollmentRule> rules;

    protected CompositeRule(EnrollmentRule... rules) { this.rules = List.of(rules); }

    protected List<RuleResult> results(EnrollmentRequest request) {
        List<RuleResult> list = new ArrayList<>();
        for (EnrollmentRule rule : rules) list.add(rule.check(request));
        return list;
    }

    protected static String reasons(List<RuleResult> results, boolean fails) {
        List<String> out = new ArrayList<>();
        for (RuleResult r : results) {
            if (fails && r instanceof RuleResult.Fail f) out.add(f.reason());
            if (!fails && r instanceof RuleResult.Defer d) out.add(d.reason());
        }
        return String.join("; ", out);
    }

    protected static boolean has(List<RuleResult> results, Class<? extends RuleResult> type) {
        return results.stream().anyMatch(type::isInstance);
    }
}
