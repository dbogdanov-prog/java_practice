public interface PaymentPolicy {
    double total(double basePrice, int months);

    String label();
}
