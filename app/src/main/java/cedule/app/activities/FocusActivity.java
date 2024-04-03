package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import cedule.app.R;
import cedule.app.utils.TimeUtils;

public class FocusActivity extends AppCompatActivity {
    public final static int STATUS_STOP = 0;
    public final static int STATUS_COUNTING = 2;

    private int status = STATUS_STOP;

    private void startCount() {
        new Thread(() -> {
            try {
                int count = 0;
                while (status != STATUS_STOP) {
                    String displayText = TimeUtils.toTimeNotation(count);
                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.tv_count)).setText(displayText);
                    });

                    Thread.sleep(1000);
                    count++;
                }
            }
            catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        findViewById(R.id.btn_start).setOnClickListener(v -> {
            if (status == STATUS_COUNTING) {
                status = STATUS_STOP;
                ((TextView) findViewById(R.id.tv_count)).setText(TimeUtils.toTimeNotation(0));
                ((ImageButton) findViewById(R.id.btn_start)).setImageResource(R.drawable.ic_play);
            }
            else if (status == STATUS_STOP) {
                status = STATUS_COUNTING;

                startCount();

                ((ImageButton) findViewById(R.id.btn_start)).setImageResource(R.drawable.ic_pause);
            }
        });

        findViewById(R.id.ib_exit).setOnClickListener(v -> finish());

        getWindow().setStatusBarColor(0x84F5F2F2);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
    }
}