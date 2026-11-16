// 1. Нарушение симметричности
class BrokenPoint {
    final int x, y;
    BrokenPoint(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        return o instanceof BrokenPoint p && x == p.x && y == p.y;
    }

    @Override
    public int hashCode() { return 31 * x + y; }
}

class BrokenColoredPoint extends BrokenPoint {
    final String color;
    BrokenColoredPoint(int x, int y, String color) { super(x, y); this.color = color; }

    @Override
    public boolean equals(Object o) {
        return o instanceof BrokenColoredPoint c && super.equals(o) && color.equals(c.color);
    }

    @Override
    public int hashCode() { return 31 * super.hashCode() + color.hashCode(); }
}

// 2. Исправление: строгое сравнение классов
class StrictPoint {
    final int x, y;
    StrictPoint(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        StrictPoint p = (StrictPoint) o;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() { return 31 * x + y; }
}

class StrictColoredPoint extends StrictPoint {
    final String color;
    StrictColoredPoint(int x, int y, String color) { super(x, y); this.color = color; }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && color.equals(((StrictColoredPoint) o).color);
    }

    @Override
    public int hashCode() { return 31 * super.hashCode() + color.hashCode(); }
}

// 3. Исправление: композиция, final value class
record Point(int x, int y) {
}

record ColoredPoint(Point point, String color) {
}

public class Main {
    public static void main(String[] args) {
        BrokenPoint p = new BrokenPoint(1, 2);
        BrokenColoredPoint cp = new BrokenColoredPoint(1, 2, "red");
        System.out.println("Broken: p.equals(cp) = " + p.equals(cp) + ", cp.equals(p) = " + cp.equals(p));

        StrictPoint sp = new StrictPoint(1, 2);
        StrictColoredPoint scp = new StrictColoredPoint(1, 2, "red");
        System.out.println("Strict: sp.equals(scp) = " + sp.equals(scp) + ", scp.equals(sp) = " + scp.equals(sp));
        StrictPoint anon = new StrictPoint(1, 2) { };
        System.out.println("Strict: sp.equals(анонимный подкласс) = " + sp.equals(anon));

        Point point = new Point(1, 2);
        ColoredPoint red = new ColoredPoint(point, "red");
        System.out.println("Composition: " + red.point().equals(point) + ", " + red.equals(new ColoredPoint(new Point(1, 2), "red")));
    }
}
