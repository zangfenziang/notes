package zju.shumi.notes.ui.agenda;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import zju.shumi.notes.MainActivity;
import zju.shumi.notes.R;
import zju.shumi.notes.modal.Item;
import zju.shumi.notes.modal.ItemsReader;
import zju.shumi.notes.modal.Time;

public class AgendaFragment extends Fragment {

    private AgendaViewModel mViewModel;

    public static AgendaFragment newInstance() {
        return new AgendaFragment();
    }

    private LinearLayout layout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(AgendaViewModel.class);
        View root = inflater.inflate(R.layout.agenda_fragment, container, false);
        TextView agenda_data = root.findViewById(R.id.agenda_date);
        layout = root.findViewById(R.id.agenda_layout);
        Day time = new Day();
        Calendar calendar = Calendar.getInstance();
        time.year = calendar.get(Calendar.YEAR);
        time.month = calendar.get(Calendar.MONTH)+1;
        time.day = calendar.get(Calendar.DAY_OF_MONTH);
        mViewModel.getDay().observe(this, new Observer<Day>() {
            @Override
            public void onChanged(Day day) {
                flush();
                if (day.year == time.year && day.month == time.month && day.day == time.day){
                    agenda_data.setText("Today");
                }
                else{
                    agenda_data.setText(String.format("%d-%d-%d", day.year, day.month, day.day));
                }
            }
        });
        agenda_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Day day = mViewModel.getDay().getValue();
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Day select = new Day();
                        select.year = year;
                        select.month = monthOfYear + 1;
                        select.day = dayOfMonth;
                        mViewModel.setDay(select);
                    }
                }, day.year, day.month - 1, day.day);
                dpd.setOkText("OK");
                dpd.setCancelText("Cancel");
                dpd.show(getFragmentManager(), "DatePicker");
            }
        });
        mViewModel.getMap().observe(this, new Observer<Map<Time, Map<String, Item>>>() {
            @Override
            public void onChanged(Map<Time, Map<String, Item>> timeMapMap) {
                flush();
            }
        });
        SharedPreferences sp = getActivity().getSharedPreferences("cache", Context.MODE_PRIVATE);
        Consumer<SharedPreferences> consumer = new Consumer<SharedPreferences>() {
            @Override
            public void accept(SharedPreferences sharedPreferences) {
                Set<String> filenames = sharedPreferences.getStringSet(MainActivity.OrgFileNames, new HashSet<>());
                final Map<Time, Map<String, Item>> map = new HashMap<>();
                filenames.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        File file = new File(Environment.getExternalStorageDirectory() + MainActivity.OrgFileDir + s + ".org");
                        try{
                            ArrayList<Item> items = ItemsReader.read(file);
                            items.forEach(new Consumer<Item>() {
                                @Override
                                public void accept(Item item) {
                                    Map<String, Item> m = map.getOrDefault(item.getDeadline(), new HashMap<>());
                                    m.put(s, item);
                                    map.put(item.getDeadline(), m);
                                }
                            });
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                mViewModel.setMap(map);
            }
        };
        mViewModel.setDay(time);
        consumer.accept(sp);
        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                consumer.accept(sp);
            }
        });
        return root;
    }

    private void flush(){
        Map<Time, Map<String, Item>> map = mViewModel.getMap().getValue();
        Map<Time, Map<String, Item>> sortMap = new LinkedHashMap<>();
        map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(new Consumer<Map.Entry<Time, Map<String, Item>>>() {
            @Override
            public void accept(Map.Entry<Time, Map<String, Item>> timeMapEntry) {
                sortMap.put(timeMapEntry.getKey(), timeMapEntry.getValue());
            }
        });
        final Day now = mViewModel.getDay().getValue();
        LocalDate cNow = LocalDate.of(now.year, now.month, now.day);
        layout.removeAllViews();
        sortMap.forEach(new BiConsumer<Time, Map<String, Item>>() {
            @Override
            public void accept(Time time, Map<String, Item> stringItemMap) {
                LocalDate cTime = LocalDate.of(time.year, time.month, time.day);
                Period p = Period.between(cNow, cTime);
                if (p.getYears() != 0 || p.getMonths() != 0 || p.getDays() > 6 || p.getDays() < -1){
                    return;
                }
                Map<String, Item> sort = new LinkedHashMap<>();
                stringItemMap.entrySet().stream().sorted(Map.Entry.comparingByValue(new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        Time t1 = o1.getDeadline();
                        Time t2 = o2.getDeadline();
                        if (t1.hour < 0){
                            if (t2.hour < 0){
                                return 0;
                            }
                            return 1;
                        }
                        if (t2.hour < 0){
                            return -1;
                        }
                        if (t1.hour == t2.hour){
                            return Integer.compare(t1.hour, t2.hour);
                        }
                        return t1.hour < t2.hour ? -1 : 1;
                    }
                })).forEachOrdered(new Consumer<Map.Entry<String, Item>>() {
                    @Override
                    public void accept(Map.Entry<String, Item> stringItemEntry) {
                        sort.put(stringItemEntry.getKey(), stringItemEntry.getValue());
                    }
                });
                sort.forEach(new BiConsumer<String, Item>() {
                    @Override
                    public void accept(String s, Item item) {

                    }
                });
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

}
