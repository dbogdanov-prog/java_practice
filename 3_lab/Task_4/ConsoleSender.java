public class ConsoleSender extends AbstractNotificationSender {
    @Override
    protected void doSend(String to, String message) {
        System.out.println("Console [" + to + "]: " + message);
    }
}
