package zju.shumi.notes.modal;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class OrgObject {
    private ArrayList<Item> items;
    OrgObject(){
        items = new ArrayList<>();
    }
    OrgObject(String str){
        this();
        // TODO: str => items
    }
    public void addItem(Item item, int index){
        items.add(index, item);
    }
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (Item item : items) {
            builder.append(item.toString());
        }
        return builder.toString();
    }
}
