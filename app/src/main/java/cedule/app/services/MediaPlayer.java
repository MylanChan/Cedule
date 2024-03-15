package cedule.app.services;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import java.io.IOException;

public class MediaPlayer {
    private android.media.MediaPlayer mediaPlayer;
    private final Context context;

    public void release() {
        if (mediaPlayer != null) mediaPlayer.release();
    }

    public void run(String assetPath) {
        try {
            mediaPlayer =  new android.media.MediaPlayer();
            AssetFileDescriptor afd = context.getAssets().openFd(assetPath);

            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

            mediaPlayer.prepare();
            mediaPlayer.start();

            afd.close();
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }

    public MediaPlayer(Context context) {
        this.context = context;
    }
}
