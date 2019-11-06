package zju.shumi.notes;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

import zju.shumi.notes.modal.Priority;
import zju.shumi.notes.modal.ShowOnTime;
import zju.shumi.notes.modal.State;
import zju.shumi.notes.modal.Time;

public class EditorViewModel extends ViewModel {
    private MutableLiveData<String> title;
    private MutableLiveData<String> filename;
    private MutableLiveData<State> state;
    private MutableLiveData<Priority> priority;
    private MutableLiveData<Set<String>> tags;
    private MutableLiveData<Time> scheduled, deadline, show, closed;
    private MutableLiveData<ShowOnTime> showOnTime;
    private MutableLiveData<String> notes;
    public EditorViewModel(){
        title = new MutableLiveData<>();
        filename = new MutableLiveData<>();
        state = new MutableLiveData<>();
        priority = new MutableLiveData<>();
        tags = new MutableLiveData<>();
        scheduled = new MutableLiveData<>();
        deadline = new MutableLiveData<>();
        showOnTime = new MutableLiveData<>();
        notes = new MutableLiveData<>();
        closed = new MutableLiveData<>();
        show = new MutableLiveData<>();
        title.setValue("");
        filename.setValue("");
        state.setValue(State.None);
        priority.setValue(Priority.None);
        tags.setValue(new HashSet<String>());
        notes.setValue("");
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public LiveData<String> getFilename() {
        return filename;
    }

    public LiveData<State> getState() {
        return state;
    }

    public LiveData<Priority> getPriority() {
        return priority;
    }

    public LiveData<Set<String>> getTags() {
        return tags;
    }

    public LiveData<Time> getScheduled() {
        return scheduled;
    }

    public LiveData<Time> getDeadeline() {
        return deadline;
    }

    public LiveData<ShowOnTime> getShowOnTime() {
        return showOnTime;
    }

    public LiveData<String> getNotes() {
        return notes;
    }

    public LiveData<Time> getClosed(){return closed;}

    public LiveData<Time> getShow() {
        return show;
    }

    public void setTitle(String title) {
        this.title.setValue(title);
    }

    public void setFilename(String filename) {
        this.filename.setValue(filename);
    }

    public void setState(State state) {
        this.state.setValue(state);
    }

    public void setPriority(Priority priority) {
        this.priority.setValue(priority);
    }

    public void setTags(Set<String> tags) {
        this.tags.setValue(tags);
    }

    public void setScheduled(Time scheduled) {
        this.scheduled.setValue(scheduled);
    }

    public void setDeadline(Time deadline) {
        this.deadline.setValue(deadline);
    }

    public void setShowOnTime(ShowOnTime showOnTime) {
        this.showOnTime.setValue(showOnTime);
    }

    public void setNotes(String notes) {
        this.notes.setValue(notes);
    }

    public void setClosed(Time closed) {
        this.closed.setValue(closed);
    }

    public void setShow(Time show) {
        this.show.setValue(show);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(state.getValue().toString());
        builder.append(" ");
        builder.append("[");
        if (priority.getValue() != Priority.None){
            builder.append("#");
        }
        builder.append(priority.getValue().toString());
        builder.append("] ");
        builder.append(title.getValue());
        if (!tags.getValue().isEmpty()){
            builder.append(" [");
            builder.append(String.join(" ", tags.getValue()));
            builder.append("]");
        }
        builder.append("\n");
        builder.append("DEADLINE: ");
        builder.append(String.format("<%s>", deadline.getValue().toString()));
        builder.append(" SCHEDULED: ");
        builder.append(String.format("<%s> ", scheduled.getValue().toString()));
        ShowOnTime time = showOnTime.getValue();
        Time showTime = show.getValue();
        if (time.type == ShowOnTime.Type.None){
            builder.append(String.format("<%s>", showTime.toString()));
        }
        else{
            builder.append(String.format("<%s %d%s>", showTime.toString(), time.num, time.repeat.toString()));
        }
        builder.append("\n");
        builder.append(notes.getValue());
        builder.append("\n");
        return builder.toString();
    }
}
