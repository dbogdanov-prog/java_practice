public class Main {
    public static void main(String[] args) {
        NotificationSender a = new EmailSender();
        a.send("hi");            // 1
        a.send((Object) "hi");   // 2
        a.send(5);               // 3

        EmailSender b = new EmailSender();
        b.send(5);               // 4
        b.send("hi");            // 5

        NotificationSender c = new ConsoleSender();
        c.send("hi");            // 6
        c.send((Object) "hi");   // 7
        c.send(5);               // 8
    }
}
