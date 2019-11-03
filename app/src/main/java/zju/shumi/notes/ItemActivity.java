package zju.shumi.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import zju.shumi.notes.R;

public class ItemActivity extends AppCompatActivity {
    public final static String FILENAME = "ITEM_ACTIVITY_FILENAME";
    private String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        filename = getIntent().getStringExtra(FILENAME);
        ActionBar actionBar = getSupportActionBar();
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
                startActivity(intent);
                break;
        }
        return true;
    }
}
