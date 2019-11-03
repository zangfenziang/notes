package zju.shumi.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class EditorActivity extends AppCompatActivity {

    public final static String INTENT_FILE_NAME = "Editor_File_Name";

    private EditorViewModel editorViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        final TextView priorityTextView = findViewById(R.id.item_priority);
        editorViewModel.getPriority().observe(this, new Observer<EditorViewModel.Priority>() {
            @Override
            public void onChanged(EditorViewModel.Priority priority) {
                priorityTextView.setText(String.format("Priority: %s", priority.toString()));
            }
        });
        editorViewModel.setState(EditorViewModel.State.None);

        final TextView tags = findViewById(R.id.item_tags);
        editorViewModel.getTags().observe(this, new Observer<Set<String>>() {
            @Override
            public void onChanged(Set<String> strings) {
                StringBuilder builder = new StringBuilder();
                for (String string : strings) {
                    builder.append(string);
                }
                tags.setText(String.format("Tags: ", builder.toString()));
            }
        });
        editorViewModel.setTags(new HashSet<String>());

        final TextView scheduled = findViewById(R.id.item_scheduled);
        editorViewModel.getScheduled().observe(this, new Observer<Time>() {
            @Override
            public void onChanged(Time time) {
                if (time != null){
                    scheduled.setText(String.format("Scheduled: %s", time.toString()));
                }
            }
        });
        Time time = new Time();
        Calendar calendar = Calendar.getInstance();
        time.year = calendar.get(Calendar.YEAR);
        time.month = calendar.get(Calendar.MONTH)+1;
        time.day = calendar.get(Calendar.DAY_OF_MONTH);
        time.hour = calendar.get(Calendar.HOUR_OF_DAY);
        time.minute = calendar.get(Calendar.MINUTE);
        editorViewModel.setScheduled(time);
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
}
