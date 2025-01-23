package cedule.app.activities;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.style.TextAlign
import cedule.app.ui.theme.CeduleTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FocusActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CeduleTheme {
                Scaffold(
                        topBar = {
                                TopAppBar(
                                        title = { Text("Task", textAlign = TextAlign.Center) },
                                        navigationIcon = {
                                                Icon(
                                                        painter = rememberVectorPainter(Icons.Filled.KeyboardArrowLeft),
                                                        contentDescription = "Leave this page",
                                                        modifier = Modifier.clickable {  }
                                )
                        }
                )
                    }
                ) { innerPadding ->


                }
            }
        }
    }
}
//public class FocusActivity extends AppCompatActivity {
//    private boolean isCounting = false;
//
//    private void handleOnClickControl() {
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (isCounting) {
//            stopCount();
//            return;
//        }
//
//        if (manager.isNotificationPolicyAccessGranted()) {
//            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE);
//            startCount();
//            return;
//        }
//        requestDndPermission();
//    }
//
//    private void stopCount() {
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        isCounting = false;
//        if (manager.isNotificationPolicyAccessGranted()) {
//            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
//        }
//
//        ((TextView) findViewById(R.id.tv_count)).setText(TimeUtils.toTimeNotation(0));
//        ((ImageButton) findViewById(R.id.ib_control)).setImageResource(R.drawable.ic_play);
//    }
//
//    private void startCount() {
//        isCounting = true;
//
//        ImageButton ibStart = findViewById(R.id.ib_control);
//        ibStart.setImageResource(R.drawable.ic_pause);
//
//        // avoid stopwatch block the UI thread
//        new Thread(() -> {
//            try {
//                int count = 0;
//
//                // run every second until user click the stop button
//                while (isCounting) {
//                    String displayText = TimeUtils.toTimeNotation(count);
//                    runOnUiThread(() -> {
//                        ((TextView) findViewById(R.id.tv_count)).setText(displayText);
//                    });
//
//                    Thread.sleep(1000);
//                    count++;
//                }
//            }
//            catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//    }
//
//    private void showDndSetting() {
//        Intent intent = new Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS");
//        startActivity(intent);
//    }
//
//    private void requestDndPermission() {
//        new AlertDialog.Builder(this)
//                .setTitle("Require permission")
//                .setMessage("to enable Do not disturb mode.")
//                .setPositiveButton("Grant", (dialog, i) -> showDndSetting())
//                .setNegativeButton("Not now", (dialog, i) -> startCount())
//                .show();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (manager.isNotificationPolicyAccessGranted()) {
//            manager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
//        }
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_focus);
//
//        getWindow().setStatusBarColor(0x84F5F2F2);
//        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
//        LayoutUtils.setBarColor(getWindow());
//
//        findViewById(R.id.ib_exit).setOnClickListener(v -> finish());
//        findViewById(R.id.ib_control).setOnClickListener(v -> handleOnClickControl());
//
//        // stop service (if running) -> remove notification + alarm
//        Intent service = new Intent(getApplicationContext(), TaskNotifyService.class);
//        getApplicationContext().stopService(service);
//    }
//}