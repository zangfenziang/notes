package zju.shumi.notes.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Map;

import zju.shumi.notes.modal.Item;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<Map<String, ArrayList<Item>>> map;

    public HomeViewModel(){
        map = new MutableLiveData<>();
    }

    public LiveData<Map<String, ArrayList<Item>>> getMap() {
        return map;
    }

    public void setMap(Map<String, ArrayList<Item>> map) {
        this.map.setValue(map);
    }
}