package zju.shumi.notes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, Notification.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);
    }
}
