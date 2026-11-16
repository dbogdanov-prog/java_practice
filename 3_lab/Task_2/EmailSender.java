public class EmailSender extends NotificationSender {
    @Override
    public void send(String msg) {
        System.out.println("EmailSender.send(String): " + msg);
    }

    // перегрузка, не переопределение
    public void send(int code) {
        System.out.println("EmailSender.send(int): " + code);
    }
}
