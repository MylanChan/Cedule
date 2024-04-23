package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


import cedule.app.R;
import cedule.app.data.Database;

public class MainActivity extends AppCompatActivity {
    private static Database database;

    public static Database getDatabase() {
        return database;
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

        database = Room.databaseBuilder(this, Database.class, "app.db")
                .createFromAsset("app.db")
                .fallbackToDestructiveMigration()
                .build();

        findViewById(R.id.btn_about).setOnClickListener(v -> {
            Intent intent = new Intent(this, DocumentActivity.class);
            intent.putExtra("type", DocumentActivity.TYPE_ABOUT);
            startActivity(intent);
        });

        findViewById(R.id.btn_story).setOnClickListener(v -> {
            Intent intent = new Intent(this, DocumentActivity.class);
            intent.putExtra("type", DocumentActivity.TYPE_STORY);
            startActivity(intent);
        });

        findViewById(R.id.btn_task).setOnClickListener(v -> {
            startActivity(new Intent(this, TaskActivity.class));
        });

        findViewById(R.id.btn_focus).setOnClickListener(v -> {
            startActivity(new Intent(this, FocusActivity.class));
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long startDate = sharedPref.getLong("StartDate", -1);
        long taskCompleted = sharedPref.getLong("TaskCompleted", 0);

        if (startDate == -1) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putLong("StartDate", System.currentTimeMillis());

            ((TextView) findViewById(R.id.tv_info)).setText(
                    "You used our service 0 days and completed " + taskCompleted + " tasks"
            );

            editor.commit();
        }
        else {
            long dayUse = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - startDate);
            ((TextView) findViewById(R.id.tv_info)).setText(
                    "You used our service " + dayUse + "days and completed " + taskCompleted + " tasks"
            );
        }

    }
}