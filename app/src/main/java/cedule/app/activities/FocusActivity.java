package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TextView;

import cedule.app.R;

public class FocusActivity extends AppCompatActivity {

    private void endCountdown() {
        TextView tvReduce = findViewById(R.id.tv_reduce);
        runOnUiThread(() -> tvReduce.setText("End"));
    }

    private void countdown(int minutes) {
        try {
            // update once in every minute
            Thread.sleep(60*1000);

            minutes -= 1;
            int finalMinutes = minutes;

            if (minutes == 0) {
                endCountdown();
                return;
            }
            TextView tvReduce = findViewById(R.id.tv_reduce);
            runOnUiThread(() -> tvReduce.setText(String.valueOf(finalMinutes)));

            countdown(minutes);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void initiaNumberPickerRange() {
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

        initiaNumberPickerRange();

        findViewById(R.id.button).setOnClickListener(v -> {
            NumberPicker npHrs = findViewById(R.id.np_hrs);
            NumberPicker npMins = findViewById(R.id.np_mins);

            int countdownInMins = npHrs.getValue() * 60 + npMins.getValue();

            // thread avoid block the UI when countdowning
            new Thread(() -> countdown(countdownInMins)).start();
        });
    }
}