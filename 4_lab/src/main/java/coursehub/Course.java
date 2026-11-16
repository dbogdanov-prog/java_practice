package coursehub;

public class Course {
    public static final int MIN_HOURS = 8;
    public static final int MAX_HOURS = 200;

    private final String title;
    private final int hours;

    public Course(String title, int hours) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is blank");
        }
        if (hours < MIN_HOURS || hours > MAX_HOURS) {
            throw new IllegalArgumentException(
                    "hours must be between " + MIN_HOURS + " and " + MAX_HOURS + ", got " + hours);
        }
        this.title = title;
        this.hours = hours;
    }

    public String getTitle() {
        return title;
    }

    public int getHours() {
        return hours;
    }

    /** Сколько часов осталось до завершения курса. */
    public int completeHours(int doneHours) {
        if (doneHours < 0 || doneHours > hours) {
            throw new IllegalArgumentException("doneHours must be between 0 and " + hours);
        }
        return hours - doneHours;
    }

    @Override
    public String toString() {
        return "Course[" + title + ", " + hours + "h]";
    }
}