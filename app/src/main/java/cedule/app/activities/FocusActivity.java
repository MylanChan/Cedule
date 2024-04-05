package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager.isNotificationPolicyAccessGranted()) {
            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        findViewById(R.id.btn_start).setOnClickListener(v -> {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (status == STATUS_COUNTING) {
                status = STATUS_STOP;
                if (manager.isNotificationPolicyAccessGranted()) {
                    manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
                ((TextView) findViewById(R.id.tv_count)).setText(TimeUtils.toTimeNotation(0));
                ((ImageButton) findViewById(R.id.btn_start)).setImageResource(R.drawable.ic_play);
            }
            else if (status == STATUS_STOP) {
                if (!manager.isNotificationPolicyAccessGranted()) {
                    new AlertDialog.Builder(this)
                            .setTitle("Require permission")
                            .setMessage("to enable Do not disturb mode.")
                            .setPositiveButton("OK", (dialogInterface, i) -> {
                                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                startActivity(intent);
                            })
                            .setNegativeButton("Not now", (dialogInterface, i) -> {
                                status = STATUS_COUNTING;

                                startCount();

                                ((ImageButton) findViewById(R.id.btn_start)).setImageResource(R.drawable.ic_pause);
                            })
                            .show();
                    return;
                }

                manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);

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