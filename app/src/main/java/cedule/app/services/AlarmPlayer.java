package cedule.app.services;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

public class AlarmPlayer {
    private MediaPlayer mediaPlayer;
    private final Context context;

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void run() {
        try {
            // MediaPlayer can play audio on device's speaker or earphone
            // Ringtone (class) cannot play audio on earphone
            mediaPlayer = new MediaPlayer();

            // use the default alarm audio of user's operating system
            Uri path = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            mediaPlayer.setDataSource(context, path);

            mediaPlayer.prepare();
            mediaPlayer.start();
        }
        catch (IOException e) {
            release();
        }
    }

    public AlarmPlayer(Context context) {
        this.context = context;
    }
}
