package cedule.app.services;

import android.app.NotificationManager;
import android.content.Context;

import cedule.app.R;

public class Notification {
    private final NotificationManager manager;
    private final android.app.Notification builder;

    public void post() {
        manager.notify((int) System.currentTimeMillis(), builder);
    }

    public Notification(Context context, String title, String desc) {
        manager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));

        builder = new android.app.Notification.Builder(context)
                        .setSmallIcon(R.drawable.ic_calendar)
                        .setContentTitle(title)
                        .setContentText(desc)
                        .build();
    }
}
