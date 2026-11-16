public class Main {
    public static void main(String[] args) {
        CourseFormat[] formats = {new OnlineFormat(), new ClassroomFormat("101"), new HybridFormat("202")};
        PaymentPolicy[] payments = {new FreePayment(), new FixedPayment(), new SubscriptionPayment()};

        for (CourseFormat f : formats) {
            for (PaymentPolicy p : payments) {
                System.out.println(new Course("Java", 100, f, p).summary(3));
            }
        }

        System.out.println("--- новые формат и оплата");
        System.out.println(new Course("Java", 100, new WebinarFormat(), new InstallmentPayment()).summary(3));
        System.out.println(new Course("Java", 100, new WebinarFormat(), new SubscriptionPayment()).summary(3));
        System.out.println(new Course("Java", 100, new OnlineFormat(), new InstallmentPayment()).summary(3));
    }
}
