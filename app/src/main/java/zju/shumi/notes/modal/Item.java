package zju.shumi.notes.modal;

import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Item {
    private int deep;
    private State state;
    private Priority priority;
    private String Title;
    private Set<String> tags;
    private Time scheduled, deadline, showOn, closed;
    private ShowOnTime showOnTime;
    private String note;

    public Time getClosed() {
        return closed;
    }

    public void setClosed(Time closed) {
        this.closed = closed;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
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

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Time getScheduled() {
        return scheduled;
    }

    public void setScheduled(Time scheduled) {
        this.scheduled = scheduled;
    }

    public Time getDeadline() {
        return deadline;
    }

    public void setDeadline(Time deadline) {
        this.deadline = deadline;
    }

    public Time getShowOn() {
        return showOn;
    }

    public void setShowOn(Time showOn) {
        this.showOn = showOn;
    }

    public ShowOnTime getShowOnTime() {
        return showOnTime;
    }

    public void setShowOnTime(ShowOnTime showOnTime) {
        this.showOnTime = showOnTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public static Item parse(String str) throws Exception{
        Item item = new Item();
        String[] result = str.split("\n");
        if (result.length < 3){
            throw new Exception("Type Error: string less than 3 line");
        }
        String line = result[0];
        String[] message = line.split(" ");
        if (message.length < 4){
            throw new Exception("Type Error: first line less than 4 block");
        }
        item.setDeep(message[0].length() - 1);
        item.setState(State.valueOf(message[1]));
        String priority = message[2];
        priority = priority.substring(1, priority.length() - 1);
        if (priority.substring(0,1).equals("#")){
            priority = priority.substring(1);
        }
        item.setPriority(Priority.valueOf(priority));
        item.setTitle(message[3]);
        Set<String> tags = new HashSet<>();
        if (message.length > 4){
            message[4] = message[4].substring(1);
            String last = message[message.length - 1];
            message[message.length - 1] = last.substring(0, last.length() - 1);
            for (int i = 4; i < message.length; i++) {
                tags.add(message[i]);
            }
        }
        item.setTags(tags);
        line = result[1];
        message = line.split("<");
        if (message.length < 4){
            throw new Exception("Type Error: second line less than 3 block");
        }
        item.setDeadline(string2time(message[1].split(">")[0]));
        item.setScheduled(string2time(message[2].split(">")[0]));
        string2showtime(
                message[message.length - 1].split(">")[0],
                new Consumer<Time>() {
                    @Override
                    public void accept(Time time) {
                        item.setShowOn(time);
                    }
                },
                new Consumer<ShowOnTime>() {
                    @Override
                    public void accept(ShowOnTime showOnTime) {
                        item.setShowOnTime(showOnTime);
                    }
                }
        );
        if (message.length > 4){
            item.setClosed(string2time(message[3].split(">")[0]));
        }
        item.setNote(result[2]);
        return item;
    }
    private static Time string2time(String str) throws Exception{
        String[] result = str.split(" ");
        Time time = new Time();
        if (result.length < 2){
            throw new Exception("Type Error: timestamp less than 2 block");
        }
        String[] day = result[0].split("-");
        time.year = Integer.parseInt(day[0]);
        time.month = Integer.parseInt(day[1]);
        time.day = Integer.parseInt(day[2]);
        if (result.length > 2){
            day = result[2].split(":");
            time.hour = Integer.parseInt(day[0]);
            time.minute = Integer.parseInt(day[1]);
        }
        return time;
    }
    private static void string2showtime(String str, Consumer<Time> one, Consumer<ShowOnTime> two) throws Exception{
        String[] result = str.split(" ");
        Time time = new Time();
        if (result.length < 2){
            throw new Exception("Type Error: timestamp less than 2 block");
        }
        String[] day = result[0].split("-");
        time.year = Integer.parseInt(day[0]);
        time.month = Integer.parseInt(day[1]);
        time.day = Integer.parseInt(day[2]);
        ShowOnTime showOnTime = new ShowOnTime();
        if (result.length > 3){
            day = result[2].split(":");
            time.hour = Integer.parseInt(day[0]);
            time.minute = Integer.parseInt(day[1]);

            showOnTime.type = ShowOnTime.Type.Repeat;
            showOnTime.num = Integer.parseInt(result[3].substring(0, 1));
            showOnTime.repeat = ShowOnTime.Repeat.valueOf(result[3].substring(1, 2));
        }
        else if (result.length > 2){
            if (result[2].charAt(1) >= 'a' && result[2].charAt(1) <= 'z'){
                showOnTime.type = ShowOnTime.Type.Repeat;
                showOnTime.num = Integer.parseInt(result[2].substring(0, 1));
                showOnTime.repeat = ShowOnTime.Repeat.valueOf(result[2].substring(1, 2));
            }
            else{
                day = result[2].split(":");
                time.hour = Integer.parseInt(day[0]);
                time.minute = Integer.parseInt(day[1]);
                showOnTime.type = ShowOnTime.Type.None;
            }
        }
        else{
            showOnTime.type = ShowOnTime.Type.None;
        }
        one.accept(time);
        two.accept(showOnTime);
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("*");
        int deep = getDeep();
        while(deep > 0){
            deep--;
            builder.append("*");
        }
        builder.append(" ");
        builder.append(getState().toString());
        builder.append(" ");
        builder.append(String.format("[%s] ", (getPriority() == Priority.None ? "" : "#" ) + getPriority().toString()));
        builder.append(getTitle());
        Set<String> tags = getTags();
        if (tags.size() > 0){
            builder.append(" [");
            builder.append(String.join(" ", tags));
            builder.append("]");
        }
        builder.append("\n");
        builder.append("DEADLINE: ");
        builder.append(String.format("<%s>", deadline.toString()));
        builder.append(" SCHEDULED: ");
        builder.append(String.format("<%s> ", scheduled.toString()));
        ShowOnTime time = showOnTime;
        Time showTime = getShowOn();
        if (time.type == ShowOnTime.Type.None){
            builder.append(String.format("<%s>", showTime.toString()));
        }
        else{
            builder.append(String.format("<%s %d%s>", showTime.toString(), time.num, time.repeat.toString()));
        }
        builder.append("\n");
        builder.append(getNote());
        builder.append("\n");
        return builder.toString();
    }
}
