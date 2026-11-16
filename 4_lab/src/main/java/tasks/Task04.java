package tasks;

public class Task04 {

    // Порядок: try -> finally -> и только потом возвращается значение
    static int returnInTry() {
        try {
            System.out.println("1. try");
            return 1;
        } finally {
            System.out.println("2. finally (выполнится ДО того, как вернётся 1)");
        }
    }

    // finally не меняет уже "запомненное" возвращаемое значение примитива
    static int finallyChangesVariable() {
        int x = 1;
        try {
            return x;       // значение 1 уже запомнено
        } finally {
            x = 2;          // на результат не влияет
        }
    }

    // throw в try -> catch -> return в catch -> finally
    static int returnInCatch() {
        try {
            System.out.println("1. try");
            throw new RuntimeException("boom");
        } catch (RuntimeException e) {
            System.out.println("2. catch: " + e.getMessage());
            return 2;
        } finally {
            System.out.println("3. finally");
        }
    }

    // ПЛОХО: return в finally прячет исключение
    @SuppressWarnings("finally")
    static int badReturnInFinally() {
        try {
            throw new RuntimeException("важная ошибка");
        } finally {
            return 99; // исключение "важная ошибка" бесследно исчезает!
        }
    }

    // ПЛОХО: throw в finally заменяет исключение из try
    @SuppressWarnings("finally")
    static void throwInFinally() {
        try {
            throw new RuntimeException("из try");
        } finally {
            throw new IllegalStateException("из finally"); // "из try" потеряно
        }
    }

    public static void main(String[] args) {
        System.out.println("--- return в try");
        System.out.println("результат: " + returnInTry());

        System.out.println("--- finally меняет переменную");
        System.out.println("результат: " + finallyChangesVariable()); // 1

        System.out.println("--- return в catch");
        System.out.println("результат: " + returnInCatch());

        System.out.println("--- return в finally (плохо)");
        System.out.println("результат: " + badReturnInFinally()); // 99, ошибки не видно

        System.out.println("--- throw в finally (плохо)");
        try {
            throwInFinally();
        } catch (RuntimeException e) {
            System.out.println("поймали: " + e.getMessage()); // "из finally"
        }
    }
}