import java.util.Locale;

public class Course {
    private final String title;
    private final double basePrice;
    private final CourseFormat format;
    private final PaymentPolicy payment;

    public Course(String title, double basePrice, CourseFormat format, PaymentPolicy payment) {
        this.title = title;
        this.basePrice = basePrice;
        this.format = format;
        this.payment = payment;
    }

    public String summary(int months) {
        return String.format(Locale.US, "%s | %s | %s | итого %.2f",
                title, format.describe(), payment.label(), payment.total(basePrice, months));
    }
}
