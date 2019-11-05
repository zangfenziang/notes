package zju.shumi.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EditorActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String INTENT_FILE_NAME = "Editor_File_Name";

    private EditorViewModel editorViewModel;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("cache_tags", MODE_PRIVATE);
        setContentView(R.layout.activity_editor);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Create Item");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        editorViewModel = new EditorViewModel();

        EditText title = findViewById(R.id.item_title);
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editorViewModel.setTitle(s.toString());
            }
        });

        final TextView filename = findViewById(R.id.item_file_name);
        editorViewModel.getFilename().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                filename.setText(String.format("Filename: %s", s));
            }
        });
        String name = getIntent().getStringExtra(INTENT_FILE_NAME);
        editorViewModel.setFilename(name);

        final TextView stateTextView = findViewById(R.id.item_state);
        editorViewModel.getState().observe(this, new Observer<EditorViewModel.State>() {
            @Override
            public void onChanged(EditorViewModel.State state) {
                stateTextView.setText(String.format("State: %s", state.toString()));
            }
        });
        editorViewModel.setState(EditorViewModel.State.None);
        stateTextView.setOnClickListener(this);

        final TextView priorityTextView = findViewById(R.id.item_priority);
        editorViewModel.getPriority().observe(this, new Observer<EditorViewModel.Priority>() {
            @Override
            public void onChanged(EditorViewModel.Priority priority) {
                priorityTextView.setText(String.format("Priority: %s", priority.toString()));
            }
        });
        editorViewModel.setState(EditorViewModel.State.None);
        priorityTextView.setOnClickListener(this);

        final TextView tags = findViewById(R.id.item_tags);
        editorViewModel.getTags().observe(this, new Observer<Set<String>>() {
            @Override
            public void onChanged(Set<String> strings) {
                StringBuilder builder = new StringBuilder();
                for (String string : strings) {
                    builder.append(" ");
                    builder.append(string);
                }
                tags.setText(String.format("Tags:%s", builder.toString()));
            }
        });
        editorViewModel.setTags(new HashSet<String>());
        tags.setOnClickListener(this);

        final TextView scheduled = findViewById(R.id.item_scheduled);
        editorViewModel.getScheduled().observe(this, new Observer<Time>() {
            @Override
            public void onChanged(Time time) {
                if (time != null){
                    scheduled.setText(String.format("Scheduled: %s", time.toString()));
                }
            }
        });
        final Time time = new Time();
        Calendar calendar = Calendar.getInstance();
        time.year = calendar.get(Calendar.YEAR);
        time.month = calendar.get(Calendar.MONTH)+1;
        time.day = calendar.get(Calendar.DAY_OF_MONTH);
        time.hour = calendar.get(Calendar.HOUR_OF_DAY);
        time.minute = calendar.get(Calendar.MINUTE);
        editorViewModel.setScheduled(time);

        final TextView deadline = findViewById(R.id.item_deadline);
        editorViewModel.getDeadeline().observe(this, new Observer<Time>() {
            @Override
            public void onChanged(Time time) {
                deadline.setText(String.format("Deadline: %s", time.toString()));
            }
        });
        deadline.setOnClickListener(this);

        final TextView showOn = findViewById(R.id.item_show_on);
        final TextView showOnAddition = findViewById(R.id.item_interval);
        final View showOnAdditionTop = findViewById(R.id.item_interval_margin_top);
        editorViewModel.getShow().observe(this, new Observer<Time>() {
            @Override
            public void onChanged(Time time) {
                showOn.setText(String.format("Show on: %s", time.toString()));
                showOnAddition.setVisibility(View.VISIBLE);
                showOnAdditionTop.setVisibility(View.VISIBLE);
            }
        });
        editorViewModel.getShowOnTime().observe(this, new Observer<ShowOnTime>() {
            @Override
            public void onChanged(ShowOnTime showOnTime) {
                showOnAddition.setText(String.format("Interval: %s", showOnTime.toString()));
            }
        });
        showOn.setOnClickListener(this);
        showOnAddition.setOnClickListener(this);

        final TextView closed = findViewById(R.id.item_closed);
        final View closed_top = findViewById(R.id.item_closed_margin_top);
        editorViewModel.getClosed().observe(this, new Observer<Time>() {
            @Override
            public void onChanged(Time time) {
                if (time == null){
                    closed.setVisibility(View.GONE);
                    closed_top.setVisibility(View.GONE);
                }
                else{
                    closed.setVisibility(View.VISIBLE);
                    closed_top.setVisibility(View.VISIBLE);
                    closed.setText(String.format("Closed: %s", time.toString()));
                }
            }
        });
        editorViewModel.setClosed(null);

        final EditText notes = findViewById(R.id.item_notes);
        notes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                editorViewModel.setNotes(s.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_title_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_save:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_state){
            final EditorViewModel.State[] states = EditorViewModel.State.values();
            String[] items = new String[states.length];
            EditorViewModel.State state = editorViewModel.getState().getValue();
            int index = 0;
            for (int i = 0; i < states.length; i++) {
                items[i] = states[i].toString();
                if (state == states[i]){
                    index = i;
                }
            }
            new AlertDialog.Builder(this)
                    .setTitle("State:")
                    .setSingleChoiceItems(items, index, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editorViewModel.setState(states[which]);
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        else if (v.getId() == R.id.item_priority){
            final EditorViewModel.Priority[] priorities = EditorViewModel.Priority.values();
            String[] array = new String[priorities.length];
            int index = 0;
            EditorViewModel.Priority priority = editorViewModel.getPriority().getValue();
            for (int i = 0; i < priorities.length; i++) {
                array[i] = priorities[i].toString();
                if (priority == priorities[i]){
                    index = i;
                }
            }
            new AlertDialog.Builder(this)
                    .setTitle("Priority:")
                    .setSingleChoiceItems(array, index, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            editorViewModel.setPriority(priorities[which]);
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        if (v.getId() == R.id.item_tags){
            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(80, 10, 80, 10);
            LinearLayout textLayout = new LinearLayout(this);
            textLayout.setOrientation(LinearLayout.HORIZONTAL);
            final EditText editText = new EditText(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            textLayout.addView(editText, params);
            Button button = new Button(this);
            button.setText("Add");
            textLayout.addView(button);
            layout.addView(textLayout, layoutParams);
            final Set<String> set = sp.getStringSet("tags", new HashSet<String>());
            final EditorActivity ts = this;
            final ArrayList<CheckBox> boxes = new ArrayList<>();
            final Set<String> select = editorViewModel.getTags().getValue();
            final Consumer<String> consumer = new Consumer<String>() {
                @Override
                public void accept(String s) {
                    CheckBox checkBox = new CheckBox(ts);
                    checkBox.setText(s);
                    if (select.contains(s)){
                        checkBox.setChecked(true);
                    }
                    layout.addView(checkBox, layoutParams);
                    boxes.add(checkBox);
                }
            };
            set.forEach(consumer);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = editText.getText().toString().trim();
                    if (!text.isEmpty() && !set.contains(text)){
                        set.add(text);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putStringSet("tags", set);
                        editor.commit();
                        consumer.accept(text);
                    }
                }
            });
            new AlertDialog.Builder(this)
                    .setTitle("Tags:")
                    .setView(layout)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final Set<String> tags = new HashSet<>();
                            boxes.forEach(new Consumer<CheckBox>() {
                                @Override
                                public void accept(CheckBox checkBox) {
                                    if (checkBox.isChecked()){
                                        tags.add(checkBox.getText().toString());
                                    }
                                }
                            });
                            editorViewModel.setTags(tags);
                        }
                    })
                    .create().show();
        }
        if (v.getId() == R.id.item_deadline){

        }
        if (v.getId() == R.id.item_show_on){

        }
        if (v.getId() == R.id.item_interval){

        }
    }
}
