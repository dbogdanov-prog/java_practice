package coursehub;

import java.util.Locale;
import java.util.regex.Pattern;

public final class CourseCode {
    private static final Pattern FORMAT = Pattern.compile("[A-Z]{2,4}-\\d{3}");

    private final String value;

    private CourseCode(String value) {
        this.value = value;
    }

    public static CourseCode of(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("course code is null");
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        if (!FORMAT.matcher(normalized).matches()) {
            throw new IllegalArgumentException("invalid course code format");
        }
        return new CourseCode(normalized);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CourseCode other && other.value.equals(value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return "CourseCode[" + value + "]";
    }
}