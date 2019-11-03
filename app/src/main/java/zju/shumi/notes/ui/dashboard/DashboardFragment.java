package zju.shumi.notes.ui.dashboard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import zju.shumi.notes.ItemActivity;
import zju.shumi.notes.MainActivity;
import zju.shumi.notes.R;

public class DashboardFragment extends Fragment implements View.OnClickListener {
    public final static String StrDateFormat = "yyyy-MM-dd HH:mm:ss";
    private DashboardViewModel dashboardViewModel;
    private SharedPreferences sp;
    private SharedPreferences map;
    private FloatingActionButton floatButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        sp = inflater.getContext().getSharedPreferences("cache", Context.MODE_PRIVATE);
        map = inflater.getContext().getSharedPreferences("cache_map", Context.MODE_PRIVATE);
        final LinearLayout layout = root.findViewById(R.id.files_layout);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(30, 10, 30, 10);
        final DashboardFragment ts = this;
        dashboardViewModel.getFileNames().observe(this, new Observer<Set<String>>() {
            @Override
            public void onChanged(Set<String> strings) {
                layout.removeAllViews();
                ArrayList<String> arr = new ArrayList<>(strings);
                final Map<String, Date> m = new HashMap<>();
                for (int i = 0; i < arr.size(); i++) {
                    String str = arr.get(i);
                    String time = map.getString(str, "");
                    SimpleDateFormat sdf = new SimpleDateFormat(StrDateFormat);
                    try{
                        m.put(str, sdf.parse(time));
                    }
                    catch (ParseException e){
                        Log.d("ParseStringError", e.getMessage());
                    }
                }
                arr.sort(new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        Date date1 = m.getOrDefault(o1, new Date());
                        Date date2 = m.getOrDefault(o2, new Date());
                        return date1.compareTo(date2);
                    }
                });
                for (String s : arr) {
                    accept(s);
                }
            }
            private void accept(final String s) {
                String time = map.getString(s, "");
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                TextView textView = new TextView(getContext());
                textView.setText(s);
                TextView textView1 = new TextView(getContext());
                textView1.setText(time);
                linearLayout.addView(textView);
                linearLayout.addView(textView1);
                linearLayout.setBackgroundResource(R.drawable.ripple);
                layout.addView(linearLayout, layoutParams);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Click", s);
                        Intent intent = new Intent(getActivity(), ItemActivity.class);
                        intent.putExtra(ItemActivity.FILENAME, s);
                        startActivity(intent);
                    }
                });
            }
        });
        Set<String> set = sp.getStringSet(MainActivity.OrgFileNames, new HashSet<String>());
        dashboardViewModel.setFileNames(set);
        floatButton = root.findViewById(R.id.filesActionButton);
        floatButton.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        if (v == floatButton){
           final EditText editText = new EditText(getContext());
           new AlertDialog.Builder(getContext()).setTitle("File name:").setView(editText)
                   .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           String filename = editText.getText().toString().trim();
                           if (!filename.equals("")){
                               String absoluteFilename = Environment.getExternalStorageDirectory() + MainActivity.OrgFileDir + filename + ".org";
                               File file = new File(absoluteFilename);
                               if (!file.exists()){
                                   try{
                                       file.createNewFile();
                                       Date date = new Date();
                                       Set<String> fileNames = sp.getStringSet(MainActivity.OrgFileNames, new HashSet<String>());
                                       Set<String> newFileNames = new HashSet<>(fileNames);
                                       SharedPreferences.Editor editor = sp.edit();
                                       SharedPreferences.Editor mapEditor = map.edit();
                                       newFileNames.add(filename);
                                       editor.putStringSet(MainActivity.OrgFileNames, newFileNames);
                                       SimpleDateFormat sdf = new SimpleDateFormat(StrDateFormat);
                                       mapEditor.putString(filename, sdf.format(date));
                                       mapEditor.commit();
                                       editor.commit();
                                       dashboardViewModel.addFileName(filename);
                                       Log.d("CreateFile", filename + " " + date.toString());
                                   }
                                   catch (IOException e){
                                       Log.d("CreateFileError", e.getMessage());
                                       new Toast(getContext()).setText(e.getMessage());
                                   }
                               }
                           }
                       }
                   })
                   .create().show();
        }
    }
}