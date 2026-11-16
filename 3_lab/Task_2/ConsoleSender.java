public class ConsoleSender extends NotificationSender {
    @Override
    public void send(String msg) {
        System.out.println("ConsoleSender.send(String): " + msg);
    }

    @Override
    public void send(Object msg) {
        System.out.println("ConsoleSender.send(Object): " + msg);
    }
}
