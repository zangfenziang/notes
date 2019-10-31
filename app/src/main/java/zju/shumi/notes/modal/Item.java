package zju.shumi.notes.modal;

public class Item {
    public enum State{
        None, TODO, DONE
    }
    public enum Priority{
        None, A, B, C, D
    }
    public class Time{
        int year, month, day, hour, minute, second;
    }
    public enum TimeType{
        None, Repeat, Period;
    }
    private int level;
    private String title;
    private State state;
    private Priority priority;
    private String[] tags;
    private Time scheduled, deadeLine, showOn, closed;
    private TimeType showOnType;
    private Time ShowOnAddition;
    private String note;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public Time getScheduled() {
        return scheduled;
    }

    public void setScheduled(Time scheduled) {
        this.scheduled = scheduled;
    }

    public Time getDeadeLine() {
        return deadeLine;
    }

    public void setDeadeLine(Time deadeLine) {
        this.deadeLine = deadeLine;
    }

    public Time getShowOn() {
        return showOn;
    }

    public void setShowOn(Time showOn) {
        this.showOn = showOn;
    }

    public Time getClosed() {
        return closed;
    }

    public void setClosed(Time closed) {
        this.closed = closed;
    }

    public TimeType getShowOnType() {
        return showOnType;
    }

    public void setShowOnType(TimeType showOnType) {
        this.showOnType = showOnType;
    }

    public Time getShowOnAddition() {
        return ShowOnAddition;
    }

    public void setShowOnAddition(Time showOnAddition) {
        ShowOnAddition = showOnAddition;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String toString(){
        return "";
    }
}
