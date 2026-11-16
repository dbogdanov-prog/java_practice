public class MetricsSender implements NotificationSender {
    private final NotificationSender delegate;
    private int calls;
    private int failures;

    public MetricsSender(NotificationSender delegate) { this.delegate = delegate; }

    @Override
    public void send(String to, String message) {
        calls++;
        try {
            delegate.send(to, message);
        } catch (RuntimeException e) {
            failures++;
            throw e;
        }
    }

    public String report() { return "calls=" + calls + ", failures=" + failures; }
}
