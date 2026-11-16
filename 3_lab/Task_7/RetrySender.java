public class RetrySender implements NotificationSender {
    private final NotificationSender delegate;
    private final int maxAttempts;

    public RetrySender(NotificationSender delegate, int maxAttempts) {
        this.delegate = delegate;
        this.maxAttempts = maxAttempts;
    }

    @Override
    public void send(String to, String message) {
        for (int attempt = 1; ; attempt++) {
            try {
                delegate.send(to, message);
                return;
            } catch (RuntimeException e) {
                if (attempt >= maxAttempts) throw e;
            }
        }
    }
}
