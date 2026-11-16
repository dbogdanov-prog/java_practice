public class LoggingSender implements NotificationSender {
    private final NotificationSender delegate;

    public LoggingSender(NotificationSender delegate) { this.delegate = delegate; }

    @Override
    public void send(String to, String message) {
        System.out.println("  LOG: отправка " + to);
        try {
            delegate.send(to, message);
            System.out.println("  LOG: успех");
        } catch (RuntimeException e) {
            System.out.println("  LOG: ошибка " + e.getMessage());
            throw e;
        }
    }
}
