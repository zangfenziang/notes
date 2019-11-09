package zju.shumi.notes.ui.agenda;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

import zju.shumi.notes.modal.Item;
import zju.shumi.notes.modal.Time;

public class AgendaViewModel extends ViewModel {
    MutableLiveData<Map<Time, Map<String, Item>>> map;
    MutableLiveData<Day> day;
    public AgendaViewModel(){
        map = new MutableLiveData<>();
        day = new MutableLiveData<>();
        map.setValue(new HashMap<>());
    }

    public LiveData<Day> getDay() {
        return day;
    }

    public void setDay(Day day) {
        this.day.setValue(day);
    }

    public LiveData<Map<Time, Map<String, Item>>> getMap() {
        return map;
    }

    public void setMap(Map<Time, Map<String, Item>> map) {
        this.map.setValue(map);
    }
}
