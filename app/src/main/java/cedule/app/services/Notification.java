package cedule.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import cedule.app.R;

public class Notification {
    public final String CHANNEL_TASKS = "tasks";
    public final String CHANNEL_EVENTS = "events";

    private final NotificationManager manager;
    private final NotificationCompat.Builder builder;

    public void post() {
        // use current milliseconds act as unique Id
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void createChannel(String id) {
        // higher API level require a Notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(id, id, NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(channel);
        }
    }

    public Notification(Context context, String channel, String title, String desc) {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(context, channel)
                .setContentTitle(title)
                .setContentText(desc);

        if (channel.equals(CHANNEL_TASKS)) {
            createChannel(channel);
            builder.setSmallIcon(R.drawable.ic_task);
            builder.setColor(Color.GRAY);
            return;
        }

        if (channel.equals(CHANNEL_EVENTS)) {
            createChannel(channel);
            builder.setSmallIcon(R.drawable.ic_calendar);
            builder.setColor(Color.MAGENTA);
        }
    }
}
