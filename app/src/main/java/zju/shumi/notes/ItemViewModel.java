package zju.shumi.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.function.Consumer;

import zju.shumi.notes.modal.Item;

public class ItemViewModel extends ViewModel {
    MutableLiveData<ArrayList<Item>> items;

    public ItemViewModel() {
        items = new MutableLiveData<>();
        items.setValue(new ArrayList<>());
    }

    public LiveData<ArrayList<Item>> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items.setValue(items);
    }

    public void addItem(Item item){
        ArrayList<Item> items = this.items.getValue();
        items.add(item);
        this.items.setValue(items);
    }
    public void addItemAfterItem(Item item, Item beforItem){
        ArrayList<Item> items = this.items.getValue();
        items.add(items.indexOf(beforItem) + 1, item);
        this.items.setValue(items);
    }
    public void setItem(Item item, Consumer<Item> c){
        ArrayList<Item> items = this.items.getValue();
        c.accept(items.get(items.indexOf(item)));
        this.items.setValue(items);
    }
}
