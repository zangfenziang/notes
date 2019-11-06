package zju.shumi.notes.modal;

import androidx.annotation.NonNull;

import java.util.Calendar;

public class Time{
    private static String[] weeks = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
    public int year, month, day, hour, minute;

    public Time(){
        this.hour = -1;
        this.minute = -1;
    }

    @NonNull
    @Override
    public String toString() {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        String week = weeks[c.get(Calendar.DAY_OF_WEEK) - 1];
        String s = String.format("%d-%02d-%02d %s", year, month, day, week);
        if (this.hour >= 0){
            s = String.format("%s %02d:%02d", s, hour, minute);
        }
        return s;
    }
}
