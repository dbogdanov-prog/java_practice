public class EmailSender extends AbstractNotificationSender {
    @Override
    protected void doSend(String to, String message) {
        System.out.println("Email -> " + to + ": " + message);
    }
}
