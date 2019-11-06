package zju.shumi.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import zju.shumi.notes.R;
import zju.shumi.notes.modal.Item;
import zju.shumi.notes.modal.ItemWriter;
import zju.shumi.notes.modal.ItemsReader;
import zju.shumi.notes.modal.Priority;
import zju.shumi.notes.modal.ShowOnTime;
import zju.shumi.notes.modal.State;

public class ItemActivity extends AppCompatActivity {
    public final static String FILENAME = "ITEM_ACTIVITY_FILENAME";
    public final static int START_EDITOR_ACTIVITY = 1;
    private String filename;
    private ItemViewModel itemViewModel;
    enum Operate{
        Add, Insert
    }
    Operate  operate = Operate.Add;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        itemViewModel = new ItemViewModel();
        final LinearLayout layout = findViewById(R.id.activity_item_content);
        itemViewModel.getItems().observe(this, new Observer<ArrayList<Item>>() {
            @Override
            public void onChanged(ArrayList<Item> items) {
                layout.removeAllViews();
                items.forEach(new Consumer<Item>() {
                    @Override
                    public void accept(Item item) {
                        LinearLayout linearLayout = new LinearLayout(ItemActivity.this);
                        linearLayout.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout head = new LinearLayout(ItemActivity.this);
                        LinearLayout.MarginLayoutParams headParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 100);
                        head.setOrientation(LinearLayout.HORIZONTAL);
                        TextView headText = new TextView(ItemActivity.this);
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
                        ImageView edit = new ImageView(ItemActivity.this);
                        edit.setImageResource(R.drawable.ic_action_edit_black);
                        headParams = new LinearLayout.LayoutParams(60, 60);
                        headParams.setMargins(20, 20, -5, 20);
                        head.addView(edit, headParams);
                        ImageView add = new ImageView(ItemActivity.this);
                        headParams = new LinearLayout.LayoutParams(80, 80);
                        headParams.setMargins(0, 10, -5, 10);
                        add.setImageResource(R.drawable.ic_action_add_black);
                        head.addView(add, headParams);
                        if (item.getState() == State.TODO){
                            ImageView done = new ImageView(ItemActivity.this);
                            done.setImageResource(R.drawable.ic_action_done_black);
                            head.addView(done, headParams);
                        }
                        else if (item.getState() == State.DONE){
                            ImageView undo = new ImageView(ItemActivity.this);
                            undo.setImageResource(R.drawable.ic_action_undo_black);
                            head.addView(undo, headParams);
                        }
                        linearLayout.addView(head);

                        LinearLayout tag = new LinearLayout(ItemActivity.this);
                        tag.setOrientation(LinearLayout.HORIZONTAL);
                        item.getTags().forEach(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                TextView textView = new TextView(ItemActivity.this);
                                LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(5, 0, 5, 0);
                                textView.setText("[" + s + "]");
                                textView.setTextSize(12);
                                tag.addView(textView, params);
                            }
                        });
                        tag.setPadding(0, 0, 0, 10);
                        linearLayout.addView(tag);

                        TextView deadline = new TextView(ItemActivity.this);
                        deadline.setText("DEADLINE: <" + item.getDeadline().toString() + ">");
                        linearLayout.addView(deadline);
                        TextView scheduled = new TextView(ItemActivity.this);
                        scheduled.setText("SCHEDULED: <" + item.getScheduled().toString() + ">");
                        linearLayout.addView(scheduled);
                        if (item.getClosed() != null){
                            TextView closed = new TextView(ItemActivity.this);
                            closed.setText("CLOSED: <" + item.getClosed().toString() + ">");
                            linearLayout.addView(closed);
                        }
                        TextView showOn = new TextView(ItemActivity.this);
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

                        TextView note = new TextView(ItemActivity.this);
                        note.setText(item.getNote());
                        note.setTextSize(16);
                        linearLayout.addView(note);

                        linearLayout.setBackgroundResource(R.drawable.ripple);
                        LinearLayout.MarginLayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(30 + 50 * item.getDeep(), 10, 30, 10);
                        layout.addView(linearLayout, params);
                    }
                });
            }
        });
        filename = getIntent().getStringExtra(FILENAME);
        ActionBar actionBar = getSupportActionBar();
        file = new File(Environment.getExternalStorageDirectory() + MainActivity.OrgFileDir + filename + ".org");
        try{
            ArrayList<Item> items = ItemsReader.read(file);
            itemViewModel.setItems(items);
        }
        catch (Exception e){
            Log.d("ItemActivity", e.getMessage());
        }
        actionBar.setTitle(filename);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items_title_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.action_add:
                Intent intent = new Intent(this, EditorActivity.class);
                intent.putExtra(EditorActivity.INTENT_FILE_NAME, filename);
                intent.putExtra(EditorActivity.INTENT_PRIORITY, 0);
                operate = Operate.Add;
                startActivityForResult(intent, START_EDITOR_ACTIVITY);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == START_EDITOR_ACTIVITY){
            if (resultCode == EditorActivity.SUCCESS){
                String result = data.getStringExtra("Item");
                try{
                    Item item = Item.parse(result);
                    if (operate == Operate.Add){
                        itemViewModel.addItem(item);
                        ItemWriter.write(file, itemViewModel.getItems().getValue());
                    }
                    else{

                    }
                }
                catch (Exception err){
                    Log.d("Parse Error", err.getMessage());
                }
            }
        }
    }
}
