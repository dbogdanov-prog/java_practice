import java.util.List;

class Product {
    public double price() { return 10; }
    public String describe() { return "product"; }
    public void handle(Object o) { System.out.println("Product.handle"); }
}

class Book extends Product {
    @Override
    public double price() { return 12; }

    @Override
    public String describe() { return "book"; }

    @Override
    public void handle(Object o) { System.out.println("Book.handle"); }
}

// компилируется без @Override, но это перегрузка
class BookBefore extends Product {
    public void handle(String s) { System.out.println("BookBefore.handle"); }
}

class PointBefore {
    final int x, y;
    PointBefore(int x, int y) { this.x = x; this.y = y; }

    public boolean equals(PointBefore p) { return x == p.x && y == p.y; }
}

class Point {
    final int x, y;
    Point(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        return o instanceof Point p && x == p.x && y == p.y;
    }

    @Override
    public int hashCode() { return 31 * x + y; }
}

public class Main {
    public static void main(String[] args) {
        Product p = new Book();
        System.out.println(p.price() + " " + p.describe());
        p.handle("x");

        Product before = new BookBefore();
        before.handle("x");

        System.out.println(List.of(new PointBefore(1, 2)).contains(new PointBefore(1, 2)));
        System.out.println(List.of(new Point(1, 2)).contains(new Point(1, 2)));
    }
}
