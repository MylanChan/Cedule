package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import cedule.app.R;
import cedule.app.services.TaskNotifyService;
import cedule.app.utils.LayoutUtils;
import cedule.app.utils.TimeUtils;

public class FocusActivity extends AppCompatActivity {
    private boolean isCounting = false;

    private void handleOnClickControl() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (isCounting) {
            stopCount();
            return;
        }

        if (manager.isNotificationPolicyAccessGranted()) {
            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
            startCount();
            return;
        }
        requestDndPermission();
    }

    private void stopCount() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        isCounting = false;
        if (manager.isNotificationPolicyAccessGranted()) {
            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
        }

        ((TextView) findViewById(R.id.tv_count)).setText(TimeUtils.toTimeNotation(0));
        ((ImageButton) findViewById(R.id.ib_control)).setImageResource(R.drawable.ic_play);
    }

    private void startCount() {
        isCounting = true;

        ImageButton ibStart = findViewById(R.id.ib_control);
        ibStart.setImageResource(R.drawable.ic_pause);

        // avoid stopwatch block the UI thread
        new Thread(() -> {
            try {
                int count = 0;

                // run every second until user click the stop button
                while (isCounting) {
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

    private void showDndSetting() {
        Intent intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS");
        startActivity(intent);
    }

    private void requestDndPermission() {
        new AlertDialog.Builder(this)
                .setTitle("Require permission")
                .setMessage("to enable Do not disturb mode.")
                .setPositiveButton("Grant", (dialog, i) -> showDndSetting())
                .setNegativeButton("Not now", (dialog, i) -> startCount())
                .show();
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

        getWindow().setStatusBarColor(0x84F5F2F2);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());

        findViewById(R.id.ib_exit).setOnClickListener(v -> finish());
        findViewById(R.id.ib_control).setOnClickListener(v -> handleOnClickControl());

        // stop service (if running) -> remove notification + alarm
        Intent service = new Intent(getApplicationContext(), TaskNotifyService.class);
        getApplicationContext().stopService(service);
    }
}