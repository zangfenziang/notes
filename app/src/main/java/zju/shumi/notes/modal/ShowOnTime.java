package zju.shumi.notes.modal;

import androidx.annotation.NonNull;

public class ShowOnTime{
    public enum Type{
        None, Repeat
    }
    public Type type;
    public enum Repeat{
        d, w, m, y
    }
    public int num;
    public Repeat repeat;

    @NonNull
    @Override
    public String toString() {
        if (type == Type.None){
            return "None";
        }
        else{
            return "+" + num + repeat.toString();
        }
    }
}
