public class ClassroomFormat implements CourseFormat {
    private final String room;

    public ClassroomFormat(String room) { this.room = room; }

    @Override
    public String describe() { return "очно, аудитория " + room; }
}
