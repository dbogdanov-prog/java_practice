package tasks;

import java.util.Map;

/*
 * БЫЛО (ошибка):
 *
 * Exception in thread "main" java.lang.NullPointerException:
 *     Cannot invoke "String.length()" because "name" is null
 *   at tasks.Task01.nameLength(Task01.java)      <- место возникновения
 *   at tasks.Task01.printReport(Task01.java)
 *   at tasks.Task01.run(Task01.java)
 *   at tasks.Task01.main(Task01.java)
 *
 * Тип:      NullPointerException
 * Сообщение: переменная name == null
 * Путь:     main -> run -> printReport -> nameLength
 *
 * Настоящая причина НЕ в последней строке (name.length()), а в run():
 * там был передан ключ "usr" (опечатка), а в Map лежит "user".
 * Map.get вернул null, и null дошёл до name.length().
 *
 * ИСПРАВЛЕНИЕ:
 * 1) исправили ключ в run();
 * 2) nameLength теперь сразу падает с понятным сообщением, если ключа нет.
 */
public class Task01 {

    public static void main(String[] args) {
        run();
    }

    static void run() {
        Map<String, String> users = Map.of("user", "Anna");
        printReport(users, "user"); // было: "usr"
    }

    static void printReport(Map<String, String> users, String key) {
        System.out.println("Длина имени: " + nameLength(users, key));
    }

    static int nameLength(Map<String, String> users, String key) {
        String name = users.get(key);
        if (name == null) {
            throw new IllegalArgumentException("No user with key: " + key);
        }
        return name.length();
    }
}