package tasks;

import coursehub.CourseImportException;

import java.util.ArrayList;
import java.util.List;

public class Task06 {

    /** Низкий уровень: просто бросает NumberFormatException. */
    static class LineParser {
        static int parseHours(String text) {
            return Integer.parseInt(text.trim());
        }
    }

    /** Уровень импорта: переводит ошибку в CourseImportException с номером записи и cause. */
    static List<Integer> importHours(List<String> lines) {
        List<Integer> result = new ArrayList<>();
        int number = 0;
        for (String line : lines) {
            number++;
            try {
                result.add(LineParser.parseHours(line));
            } catch (NumberFormatException e) { // ловим только нужный тип, не Exception
                throw new CourseImportException(number, "hours is not a number", e);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            importHours(List.of("40", "abc", "20"));
        } catch (CourseImportException e) {
            System.out.println(e.getMessage());
            System.out.println("номер записи: " + e.getRecordNumber());
            System.out.println("исходная причина: " + e.getCause());
        }
    }
}