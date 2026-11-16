public class NotificationSender {
    public void send(String msg) {
        System.out.println("NotificationSender.send(String): " + msg);
    }

    public void send(Object msg) {
        System.out.println("NotificationSender.send(Object): " + msg);
    }
}
