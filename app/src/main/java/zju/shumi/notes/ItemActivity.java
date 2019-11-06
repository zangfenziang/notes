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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.io.File;
import java.util.ArrayList;

import zju.shumi.notes.R;
import zju.shumi.notes.modal.Item;
import zju.shumi.notes.modal.ItemWriter;
import zju.shumi.notes.modal.ItemsReader;

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
        itemViewModel.getItems().observe(this, new Observer<ArrayList<Item>>() {
            @Override
            public void onChanged(ArrayList<Item> items) {
                // TODO
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
