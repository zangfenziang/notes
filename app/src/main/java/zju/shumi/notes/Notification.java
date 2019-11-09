package zju.shumi.notes;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import zju.shumi.notes.modal.Item;
import zju.shumi.notes.modal.ItemsReader;
import zju.shumi.notes.modal.ShowOnTime;
import zju.shumi.notes.modal.State;
import zju.shumi.notes.modal.Time;

public class Notification extends Service {
    private static final String TAG = "NotificationService";
    public Notification() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "StartService");
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + 58 * 1000;
        Intent i = new Intent(this, NotificationAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        checkItems();
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkItems(){
        SharedPreferences sp = getSharedPreferences("cache", MODE_PRIVATE);
        Set<String> filenames = sp.getStringSet(MainActivity.OrgFileNames, new HashSet<>());
        filenames.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                File file = new File(Environment.getExternalStorageDirectory() + MainActivity.OrgFileDir + s + ".org");
                try{
                    ArrayList<Item> items = ItemsReader.read(file);
                    items.forEach(new Consumer<Item>() {
                        @Override
                        public void accept(Item item) {
                            if (item.getState() == State.TODO){
                                checkItem(item);
                            }
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private int id = 10001;

    private void checkItem(Item item){
        ShowOnTime showOnTime = item.getShowOnTime();
        Time show = item.getShowOn();
        Time deadline = item.getDeadline();
        LocalDate now = LocalDate.now();
        LocalDate time = LocalDate.of(show.year, show.month, show.day);
        LocalDate dead = LocalDate.of(deadline.year, deadline.month, deadline.day);
        if (now.isAfter((dead))){
            return;
        }
        if (showOnTime.type != ShowOnTime.Type.None){
            while(now.isAfter(time)) {
                switch (showOnTime.repeat) {
                    case d:
                        time = time.plusDays(showOnTime.num);
                        break;
                    case m:
                        time = time.plusMonths(showOnTime.num);
                        break;
                    case w:
                        time = time.plusWeeks(showOnTime.num);
                        break;
                    case y:
                        time = time.plusYears(showOnTime.num);
                        break;
                }
            }
        }
        Period p = Period.between(time, now);
        if (p.getDays() == 0 && p.getMonths() == 0 && p.getYears() == 0){
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int s = calendar.get(Calendar.SECOND);
            int now_clock = hour * 60 + minute;
            int clock;
            if (show.hour < 0){
                clock = 9 * 60;
            }
            else{
                clock = show.hour * 60 + show.minute;
            }
            if (now_clock == clock){
                push(++id, item.getTitle(), String.format("note: %s deadline: <%s>", item.getNote(), item.getDeadline()));
            }
        }
    }

    private void push(int id, String title, String message){
        String channel_id = "notes_channel_01";
        String name= "notification_name";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        android.app.Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channel_id, name, NotificationManager.IMPORTANCE_LOW);
            Log.i(TAG, mChannel.toString());
            notificationManager.createNotificationChannel(mChannel);
            notification = new android.app.Notification.Builder(this)
                    .setChannelId(channel_id)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher).build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setOngoing(true);
            notification = notificationBuilder.build();
        }
        notificationManager.notify(id, notification);
    }
}
