public class HybridFormat implements CourseFormat {
    private final String room;

    public HybridFormat(String room) { this.room = room; }

    @Override
    public String describe() { return "гибрид, аудитория " + room + " + онлайн"; }
}
