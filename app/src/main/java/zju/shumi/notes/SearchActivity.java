package zju.shumi.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import zju.shumi.notes.modal.Item;
import zju.shumi.notes.modal.ItemsReader;
import zju.shumi.notes.modal.Priority;
import zju.shumi.notes.modal.ShowOnTime;
import zju.shumi.notes.modal.State;
import zju.shumi.notes.modal.Time;

public class SearchActivity extends AppCompatActivity {

    private SearchViewModel searchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Search");
        searchViewModel = new SearchViewModel();
        LinearLayout layout = findViewById(R.id.search_layout);
        searchViewModel.getSearch().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                layout.removeAllViews();
                LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(30, 10, 30, 10);
                Map<String, ArrayList<Item>> map = searchViewModel.getMap().getValue();
                map.forEach(new BiConsumer<String, ArrayList<Item>>() {
                    @Override
                    public void accept(String name, ArrayList<Item> items) {
                        ArrayList<Item> result = new ArrayList<>();
                        items.forEach(new Consumer<Item>() {
                            @Override
                            public void accept(Item item) {
                                if (item.getTitle().contains(s)){
                                    result.add(item);
                                }
                            }
                        });
                        if (result.size() > 0){
                            TextView filename = new TextView(SearchActivity.this);
                            filename.setText(name);
                            layout.addView(filename, params);
                            result.forEach(new Consumer<Item>() {
                                @Override
                                public void accept(Item item) {
                                    LinearLayout linearLayout = new LinearLayout(SearchActivity.this);
                                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                                    LinearLayout head = new LinearLayout(SearchActivity.this);
                                    LinearLayout.MarginLayoutParams headParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 100);
                                    head.setOrientation(LinearLayout.HORIZONTAL);
                                    TextView headText = new TextView(SearchActivity.this);
                                    StringBuilder builder = new StringBuilder();
                                    if (item.getState() == State.TODO){
                                        builder.append("<font color='#FF0000'>").append(item.getState().toString()).append("</font>");
                                    }
                                    else{
                                        builder.append(item.getState().toString());
                                    }
                                    builder.append(" [");
                                    if (item.getPriority() != Priority.None){
                                        builder.append("#");
                                    }
                                    builder.append(item.getPriority().toString());
                                    builder.append("] ");
                                    builder.append(item.getTitle());
                                    headText.setText(Html.fromHtml(builder.toString()));
                                    headText.setTextSize(24);
                                    head.addView(headText, headParams);
//                        ImageView edit = new ImageView(ItemActivity.this);
//                        edit.setImageResource(R.drawable.ic_action_edit_black);
//                        headParams = new LinearLayout.LayoutParams(60, 60);
//                        headParams.setMargins(20, 20, -5, 20);
//                        head.addView(edit, headParams);
//                                    ImageView add = new ImageView(SearchActivity.this);
                                    headParams = new LinearLayout.LayoutParams(60, 60);
                                    headParams.setMargins(20, 20, -5, 20);
//                                    add.setImageResource(R.drawable.ic_action_add_black);
//                                    head.addView(add, headParams);
//                                    add.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            operate = ItemActivity.Operate.Insert;
//                                            beforeItem = item;
//                                            Intent intent = new Intent(ItemActivity.this, EditorActivity.class);
//                                            intent.putExtra(EditorActivity.INTENT_FILE_NAME, filename);
//                                            intent.putExtra(EditorActivity.INTENT_PRIORITY, item.getDeep() + 1);
//                                            startActivityForResult(intent, START_EDITOR_ACTIVITY);
//                                        }
//                                    });
                                    headParams = new LinearLayout.LayoutParams(80, 80);
                                    headParams.setMargins(0, 10, -5, 10);
//                                    if (item.getState() == State.TODO){
//                                        ImageView done = new ImageView(ItemActivity.this);
//                                        done.setImageResource(R.drawable.ic_action_done_black);
//                                        head.addView(done, headParams);
//                                        done.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                itemViewModel.setItem(item, new Consumer<Item>() {
//                                                    @Override
//                                                    public void accept(Item item) {
//                                                        Calendar calendar = Calendar.getInstance();
//                                                        Time time = new Time();
//                                                        time.year = calendar.get(Calendar.YEAR);
//                                                        time.month = calendar.get(Calendar.MONTH)+1;
//                                                        time.day = calendar.get(Calendar.DAY_OF_MONTH);
//                                                        time.hour = calendar.get(Calendar.HOUR_OF_DAY);
//                                                        time.minute = calendar.get(Calendar.MINUTE);
//                                                        item.setClosed(time);
//                                                        item.setState(State.DONE);
//                                                    }
//                                                });
//                                            }
//                                        });
//                                    }
//                                    else if (item.getState() == State.DONE){
//                                        ImageView undo = new ImageView(ItemActivity.this);
//                                        undo.setImageResource(R.drawable.ic_action_undo_black);
//                                        head.addView(undo, headParams);
//                                        undo.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                itemViewModel.setItem(item, new Consumer<Item>() {
//                                                    @Override
//                                                    public void accept(Item item) {
//                                                        item.setClosed(null);
//                                                        item.setState(State.TODO);
//                                                    }
//                                                });
//                                            }
//                                        });
//                                    }
                                    linearLayout.addView(head);

                                    LinearLayout tag = new LinearLayout(SearchActivity.this);
                                    tag.setOrientation(LinearLayout.HORIZONTAL);
                                    item.getTags().forEach(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) {
                                            TextView textView = new TextView(SearchActivity.this);
                                            LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                            params.setMargins(5, 0, 5, 0);
                                            textView.setText("[" + s + "]");
                                            textView.setTextSize(12);
                                            tag.addView(textView, params);
                                        }
                                    });
                                    tag.setPadding(0, 0, 0, 10);
                                    linearLayout.addView(tag);

                                    TextView deadline = new TextView(SearchActivity.this);
                                    deadline.setText("DEADLINE: <" + item.getDeadline().toString() + ">");
                                    linearLayout.addView(deadline);
                                    TextView scheduled = new TextView(SearchActivity.this);
                                    scheduled.setText("SCHEDULED: <" + item.getScheduled().toString() + ">");
                                    linearLayout.addView(scheduled);
                                    if (item.getClosed() != null){
                                        TextView closed = new TextView(SearchActivity.this);
                                        closed.setText("CLOSED: <" + item.getClosed().toString() + ">");
                                        closed.setTextSize(16);
                                        linearLayout.addView(closed);
                                    }
                                    TextView showOn = new TextView(SearchActivity.this);
                                    String str = "<" + item.getShowOn().toString();
                                    if (item.getShowOnTime().type != ShowOnTime.Type.None){
                                        str += " " + item.getShowOnTime().num + item.getShowOnTime().repeat.toString();
                                    }
                                    str += ">";
                                    showOn.setText(str);
                                    linearLayout.addView(showOn);
                                    deadline.setTextSize(16);
                                    scheduled.setTextSize(16);
                                    showOn.setTextSize(16);

                                    TextView note = new TextView(SearchActivity.this);
                                    note.setText(item.getNote());
                                    note.setTextSize(16);
                                    linearLayout.addView(note);

                                    linearLayout.setBackgroundResource(R.drawable.ripple);
                                    LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    params.setMargins(30, 10, 30, 10);
                                    layout.addView(linearLayout, params);
                                }
                            });
                        }
                    }
                });
            }
        });
        SharedPreferences sp = getSharedPreferences("cache", Context.MODE_PRIVATE);
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
                            map.put(s, items);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                searchViewModel.setMap(map);
            }
        };
        consumer.accept(sp);
        searchViewModel.setSearch("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search_view);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setIconified(false);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchViewModel.setSearch("");
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchViewModel.setSearch(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
