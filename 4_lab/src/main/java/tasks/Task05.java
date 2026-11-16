package tasks;

import java.util.Arrays;

public class Task05 {

    static class TestResource implements AutoCloseable {
        private final String name;
        private final boolean failOnUse;
        private final boolean failOnClose;

        TestResource(String name, boolean failOnUse, boolean failOnClose) {
            this.name = name;
            this.failOnUse = failOnUse;
            this.failOnClose = failOnClose;
            System.out.println("open " + name);
        }

        void use() {
            System.out.println("use " + name);
            if (failOnUse) {
                throw new IllegalStateException("use failed: " + name);
            }
        }

        @Override
        public void close() {
            System.out.println("close " + name);
            if (failOnClose) {
                throw new IllegalArgumentException("close failed: " + name);
            }
        }
    }

    static void run(String title, boolean failOnUse, boolean failOnClose) {
        System.out.println("--- " + title);
        try (TestResource r = new TestResource("R", failOnUse, failOnClose)) {
            r.use();
        } catch (RuntimeException e) {
            System.out.println("основное исключение: " + e.getMessage());
            System.out.println("suppressed: " + Arrays.toString(e.getSuppressed()));
        }
    }

    public static void main(String[] args) {
        run("успех", false, false);                 // open, use, close
        run("ошибка в use", true, false);           // close всё равно вызван
        run("ошибка в close", false, true);         // основным станет ошибка close
        run("ошибка и в use, и в close", true, true); // основное: use, suppressed: close

        System.out.println("--- два ресурса: закрываются в обратном порядке");
        try (TestResource a = new TestResource("A", false, false);
             TestResource b = new TestResource("B", false, false)) {
            a.use();
            b.use();
        }
    }
}