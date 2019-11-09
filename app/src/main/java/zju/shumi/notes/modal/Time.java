package zju.shumi.notes.modal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public class Time implements Comparable<Time>{
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
        c.set(year, month - 1, day);
        String week = weeks[c.get(Calendar.DAY_OF_WEEK) - 1];
        String s = String.format("%d-%02d-%02d %s", year, month, day, week);
        if (this.hour >= 0){
            s = String.format("%s %02d:%02d", s, hour, minute);
        }
        return s;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this){
            return true;
        }
        if (obj instanceof Time){
            Time time = (Time)obj;
            return time.year == this.year && time.month == this.month && time.day == this.day;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return year * 366 + month * 32 + day;
    }

    @Override
    public int compareTo(Time o) {
        if (year == o.year){
            if (month == o.month){
                if (day == o.day){
                    return 0;
                }
                return day < o.day ? -1 : 1;
            }
            return month < o.month ? -1 : 1;
        }
        return year < o.year ? -1 : 1;
    }
}
