package zju.shumi.notes.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import zju.shumi.notes.MainActivity;
import zju.shumi.notes.R;
import zju.shumi.notes.modal.Item;
import zju.shumi.notes.modal.ItemsReader;
import zju.shumi.notes.modal.State;
import zju.shumi.notes.modal.Time;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        LinearLayout layout = root.findViewById(R.id.todo_layout);
        homeViewModel.getMap().observe(this, new Observer<Map<String, ArrayList<Item>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<Item>> stringArrayListMap) {
                layout.removeAllViews();
                LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 10, 30, 10);
                stringArrayListMap.forEach(new BiConsumer<String, ArrayList<Item>>() {
                    @Override
                    public void accept(String s, ArrayList<Item> items) {
                        if (items.size() == 0){
                            return;
                        }
                        TextView textView = new TextView(getContext());
                        textView.setText(s);
                        layout.addView(textView, params);
                        items.forEach(new Consumer<Item>() {
                            @Override
                            public void accept(Item item) {
                                Time time = item.getDeadline();
                                LocalDate cTime = LocalDate.of(time.year, time.month, time.day);
                                LocalDate cNow = LocalDate.now();
                                Period p = Period.between(cNow, cTime);
                                LinearLayout linearLayout = new LinearLayout(getContext());
                                linearLayout.setBackgroundResource(R.drawable.ripple);
                                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                                ImageView imageView = new ImageView(getContext());
                                if (item.getState() == State.DONE){
                                    imageView.setImageResource(R.drawable.ic_action_agenda_done);
                                }
                                else if (item.getState() == State.None){
                                    imageView.setImageResource(R.drawable.ic_action_agenda_none);
                                }
                                else if (p.getYears() < 0 || p.getMonths() < 0 || p.getDays() < 0){
                                    imageView.setImageResource(R.drawable.ic_action_agenda_timeout);
                                }
                                else{
                                    imageView.setImageResource(R.drawable.ic_action_agenda_todo);
                                }
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                                layoutParams.rightMargin = 30;
                                linearLayout.addView(imageView, layoutParams);
                                LinearLayout mLinearLayout = new LinearLayout(getContext());
                                layoutParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                                layoutParams.weight = 1;
                                mLinearLayout.setOrientation(LinearLayout.VERTICAL);
                                TextView filename = new TextView(getContext());
                                filename.setText(s);
                                mLinearLayout.addView(filename);
                                TextView title = new TextView(getContext());
                                title.getPaint().setFakeBoldText(true);
                                title.setTextSize(16);
                                title.setText(item.getTitle());
                                mLinearLayout.addView(title);
                                linearLayout.addView(mLinearLayout, layoutParams);
                                TextView textView = new TextView(getContext());
                                textView.getPaint().setFakeBoldText(true);
                                textView.setText(item.getDeadline().toString());
                                layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                                linearLayout.addView(textView, layoutParams);
                                layout.addView(linearLayout, params);
                            }
                        });
                    }
                });
            }
        });
        SharedPreferences sp = getActivity().getSharedPreferences("cache", Context.MODE_PRIVATE);
        Consumer<SharedPreferences> consumer = new Consumer<SharedPreferences>() {
            @Override
            public void accept(SharedPreferences sharedPreferences) {
                Set<String> filenames = sharedPreferences.getStringSet(MainActivity.OrgFileNames, new HashSet<>());
                final Map<String, ArrayList<Item>> map = new HashMap<>();
                filenames.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        File file = new File(Environment.getExternalStorageDirectory() + MainActivity.OrgFileDir + s + ".org");
                        try{
                            ArrayList<Item> items = ItemsReader.read(file);
                            ArrayList<Item> todoList = new ArrayList<>();
                            items.forEach(new Consumer<Item>() {
                                @Override
                                public void accept(Item item) {
                                    if (item.getState() == State.TODO){
                                        todoList.add(item);
                                    }
                                }
                            });
                            map.put(s, todoList);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                homeViewModel.setMap(map);
            }
        };
        consumer.accept(sp);
        sp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                consumer.accept(sp);
            }
        });
        return root;
    }
}