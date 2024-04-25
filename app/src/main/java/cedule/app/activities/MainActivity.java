package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


import cedule.app.R;
import cedule.app.data.Database;
import cedule.app.utils.LayoutUtils;
import cedule.app.utils.TimeUtils;

public class MainActivity extends AppCompatActivity {
    private static Database database;

    public static Database getDatabase() {
        return database;
    }

    boolean isOnBackToastShowed = false;

    private void loadData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long startDate = prefs.getLong("StartDate", -1);
        long taskCompleted = prefs.getLong("TaskCompleted", 0);

        if (startDate == -1) {
            SharedPreferences.Editor editor = prefs.edit();

            Calendar calendar = Calendar.getInstance();
            TimeUtils.setMidNight(calendar);

            editor.putLong("StartDate", calendar.getTimeInMillis());

            TextView tvDay = findViewById(R.id.tv_day);
            tvDay.setText("Today is your first day to use Cedule!");

            editor.apply();
        }
        else {
            long dayUse = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - startDate);

            TextView tvDay = findViewById(R.id.tv_day);
            tvDay.setText("You used our service " + dayUse + " days");

            TextView tvTask = findViewById(R.id.tv_task);
            tvTask.setText(taskCompleted + " tasks completed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long taskCompleted = prefs.getLong("TaskCompleted", 0);

        TextView tvTask = findViewById(R.id.tv_task);
        tvTask.setText(taskCompleted + " tasks completed");
    }

    @Override
    public void onBackPressed() {
        // user need to click back button twice to exit app
        if (isOnBackToastShowed) {
            super.onBackPressed();
            return;
        }

        isOnBackToastShowed = true;
        Toast.makeText(this, "Please again to exit app", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(()-> {
            isOnBackToastShowed = false;
        }, 2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // avoid database exists 2 instances at the same time
        if (database != null) database.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setNavigationBarColor(0x75E8E8E8);
        LayoutUtils.setBarColor(getWindow());

        database = Room.databaseBuilder(this, Database.class, "app.db")
                .createFromAsset("app.db")
                .fallbackToDestructiveMigration()
                .build();

        findViewById(R.id.ll_about).setOnClickListener(v -> {
            startActivity(new Intent(this, AboutUsActivity.class));
        });

        findViewById(R.id.ll_story).setOnClickListener(v -> {
            startActivity(new Intent(this, StoryBehindActivity.class));
        });

        Button btnTask = findViewById(R.id.btn_task);
        btnTask.setOnClickListener(v -> startActivity(new Intent(this, TaskActivity.class)));

        Button btnFocus = findViewById(R.id.btn_focus);
        btnFocus.setOnClickListener(v -> startActivity(new Intent(this, FocusActivity.class)));

        loadData();
    }
}