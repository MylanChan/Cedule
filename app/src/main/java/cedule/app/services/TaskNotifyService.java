package cedule.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import cedule.app.R;
import cedule.app.activities.FocusActivity;

public class TaskNotifyService extends Service {
    private AlarmPlayer player;

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.release();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                new Intent(getApplicationContext(), FocusActivity.class),
                PendingIntent.FLAG_IMMUTABLE);

        NotificationManager manager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel("Cedule", "Cedule", NotificationManager.IMPORTANCE_DEFAULT);

            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), "Cedule")
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_task)
                    .setColor(Color.GRAY)
                    .setContentTitle("Cedule")
                    .setContentText("Time to work");

        if (player != null) player.release();
        player = new AlarmPlayer(getApplicationContext());
        player.run();

        startForeground(1, builder.build());
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}