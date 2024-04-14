package cedule.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


public class TaskNotifyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        Intent nextActivity = new Intent(context, TaskNotifyService.class);
        nextActivity.replaceExtras(intent.getExtras());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(nextActivity);
            return;
        }
        context.startService(nextActivity);
    }
}
