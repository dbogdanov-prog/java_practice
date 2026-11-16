interface Walker {
    default String move() { return "иду"; }
}

interface Swimmer {
    default String move() { return "плыву"; }
}

class Duck implements Walker, Swimmer {
    @Override
    public String move() {
        return Walker.super.move() + " и " + Swimmer.super.move();
    }
}

public class Main {
    public static void main(String[] args) {
        System.out.println(new Duck().move());
    }
}
