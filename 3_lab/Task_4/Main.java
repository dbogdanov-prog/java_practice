public class Main {
    public static void main(String[] args) {
        AbstractNotificationSender[] senders = {new EmailSender(), new ConsoleSender()};
        for (AbstractNotificationSender s : senders) {
            s.send("user@mail.com", "Курс начался");
        }
        try {
            senders[0].send(" ", "Привет");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}
