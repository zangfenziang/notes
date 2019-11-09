package zju.shumi.notes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Map;

import zju.shumi.notes.modal.Item;

public class SearchViewModel extends ViewModel {
    MutableLiveData<Map<String, ArrayList<Item>>> map;
    MutableLiveData<String> search;
    public SearchViewModel(){
        search = new MutableLiveData<>();
        map = new MutableLiveData<>();
    }

    public LiveData<Map<String, ArrayList<Item>>> getMap() {
        return map;
    }

    public void setMap(Map<String, ArrayList<Item>> map) {
        this.map.setValue(map);
    }

    public LiveData<String> getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search.setValue(search);
    }
}
