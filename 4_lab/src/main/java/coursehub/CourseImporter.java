package coursehub;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/** Формат строки: "название;часы". */
public class CourseImporter {
    private int processed;

    /** Сколько записей успешно обработано. */
    public int getProcessed() {
        return processed;
    }

    public void importInto(Reader source, List<Course> target) {
        List<Course> buffer = new ArrayList<>(); // результат копим отдельно
        processed = 0;

        try (BufferedReader in = new BufferedReader(source)) { // close() вызовется сам
            String line;
            int number = 0;
            while ((line = in.readLine()) != null) {
                number++;
                buffer.add(parseLine(line, number));
                processed = number;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("cannot read or close source", e);
        }

        target.addAll(buffer); // публикуем только если ВСЁ прошло успешно
    }

    private Course parseLine(String line, int number) {
        String[] parts = line.split(";", -1);
        if (parts.length != 2) {
            throw new CourseImportException(number, "expected format: title;hours", null);
        }
        try {
            return new Course(parts[0].trim(), Integer.parseInt(parts[1].trim()));
        } catch (IllegalArgumentException e) { // NumberFormatException тоже сюда входит
            throw new CourseImportException(number, "invalid record", e);
        }
    }
}