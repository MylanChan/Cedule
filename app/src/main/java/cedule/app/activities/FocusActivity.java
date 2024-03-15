package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;

import cedule.app.R;

public class FocusActivity extends AppCompatActivity {
    public final static int STATUS_NORMAL = 0;
    public final static int STATUS_RUNNING = 1;
    public final static int STATUS_PAUSED = 2;

    private int status = STATUS_NORMAL;

    private String toTimeString(int seconds) {
        int hrs = (int) (seconds / 3600.);
        int min = seconds/60 - hrs*60;
        int sec = seconds - hrs * 3600 - min * 60;

        return hrs + " hrs " +  min + " mins " + sec + " sec ";
    }

    private void endCountdown() {
        TextView tvReduce = findViewById(R.id.tv_reduce);
        runOnUiThread(() -> tvReduce.setText("End"));
    }

    private void countdown(int minutes) {
        try {
            // update once in every minute
            Thread.sleep(1000);

            minutes -= 1;
            int finalMinutes = minutes;

            if (minutes < 0) {
                endCountdown();
                return;
            }
            TextView tvReduce = findViewById(R.id.tv_reduce);
            runOnUiThread(() -> {
                tvReduce.setText(toTimeString(finalMinutes));
            });

            countdown(minutes);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initNumberPickerRange() {
        ((NumberPicker) findViewById(R.id.np_hrs))
                .setMinValue(0);

        ((NumberPicker) findViewById(R.id.np_hrs))
                .setMaxValue(23);

        ((NumberPicker) findViewById(R.id.np_mins))
                .setMinValue(0);

        ((NumberPicker) findViewById(R.id.np_mins))
                .setMaxValue(59);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        initNumberPickerRange();

        findViewById(R.id.button).setOnClickListener(v -> {
            NumberPicker npHrs = findViewById(R.id.np_hrs);
            NumberPicker npMins = findViewById(R.id.np_mins);

            int countdownInMins = npHrs.getValue() * 60 + npMins.getValue();

            // thread avoid block the UI when countdown
            new Thread(() -> {
                TextView tvReduce = findViewById(R.id.tv_reduce);

                runOnUiThread(() -> {
                    tvReduce.setText(toTimeString(countdownInMins*60-1));
                });
                countdown(countdownInMins*60-1);
            }).start();
        });
    }
}