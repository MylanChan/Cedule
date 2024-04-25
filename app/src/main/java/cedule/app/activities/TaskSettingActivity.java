package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cedule.app.R;
import cedule.app.data.entities.Category;
import cedule.app.data.entities.Task;
import cedule.app.dialogs.ColorDialog;
import cedule.app.services.TaskNotifyReceiver;
import cedule.app.services.TaskNotifyService;
import cedule.app.utils.LayoutUtils;
import cedule.app.utils.TimeUtils;

public class TaskSettingActivity extends AppCompatActivity {
    private boolean isNotify = false;
    private Long startDate = null;
    private Integer startTime = null;

    private void setPickCategoryColorListener() {
        getSupportFragmentManager().setFragmentResultListener("pickColor", this, (key, result) -> {
            if (result.containsKey("color")) {
                int color = result.getInt("color", Color.RED);
                findViewById(R.id.ll_color).setBackgroundTintList(ColorStateList.valueOf(color));
            }
        });
    }

    private void setCategoryTextChangeListener() {
        AutoCompleteTextView atvCategory = findViewById(R.id.atv_category);

        atvCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String catName = atvCategory.getText().toString();

                // color picker show only if the user enters something in the category field
                if (catName.length() >= 1) {
                    findViewById(R.id.ll_color_picker).setVisibility(View.VISIBLE);

                    // get color from the local database based on the category name the user entered
                    new Thread(() -> {
                        Category category = MainActivity.getDatabase().categoryDAO().getByName(catName);
                        runOnUiThread(() -> {
                            LinearLayout llColor = findViewById(R.id.ll_color);

                            if (category != null) {
                                llColor.setBackgroundTintList(ColorStateList.valueOf(category.color));
                            }
                            else {
                                // category name does not exists
                                // apply a default color
                                llColor.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            }
                        });
                    }).start();
                }
                else {
                    findViewById(R.id.ll_color_picker).setVisibility(View.GONE);
                }
            }
        });
    }

    private void postAlarm(long timestamp) {
        Intent intent = new Intent(getApplicationContext(), TaskNotifyReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestamp, pi);
    }

    private int getCategoryId(String name) {
        int color = findViewById(R.id.ll_color).getBackgroundTintList().getDefaultColor();
        MainActivity.getDatabase().categoryDAO().insertUpdateCategory(name.toLowerCase(), color);

        return MainActivity.getDatabase().categoryDAO().getByName(name).id;
    }

    private void updatePref() {
        CheckBox cbInput = findViewById(R.id.cb_input);

        SharedPreferences sharedPref = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong("TaskCompleted",
                sharedPref.getLong("TaskCompleted", 0)+(cbInput.isChecked() ? 1 : -1));

        editor.apply();
    }

    private void saveTask() {
        if (startDate == null && startTime != null) {
            Calendar calendar = Calendar.getInstance();
            TimeUtils.setMidNight(calendar);
            calendar.add(Calendar.MILLISECOND, startTime);

            if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
                calendar.add(Calendar.DATE, +1);
            }

            startDate = calendar.getTimeInMillis();
        }

        new Thread(() -> {
            // get task title if user entered
            EditText etTaskName = findViewById(R.id.et_task_name);

            String title = etTaskName.getText().toString();
            if (title.isEmpty()) title = null;

            // get category id if user entered a name to the field
            AutoCompleteTextView atcCategory = findViewById(R.id.atv_category);
            String catName = atcCategory.getText().toString().toLowerCase();

            Integer categoryId = catName.isEmpty() ? null : getCategoryId(catName);

            EditText etNote = findViewById(R.id.et_note);
            String note = etNote.getText().toString();

            CheckBox cbInput = findViewById(R.id.cb_input);


            if (getIntent().hasExtra("taskId")) {
                int taskId = getIntent().getExtras().getInt("taskId", -1);
                Task task = MainActivity.getDatabase().tasksDAO().getById(taskId);

                if ((task.isDone == 1) != cbInput.isChecked()) {
                    updatePref();
                }

                MainActivity.getDatabase().tasksDAO().update(
                        taskId,
                        title, categoryId,
                        startDate, startTime,
                        cbInput.isChecked() ? 1 : 0,
                        isNotify ? 1 : 0,
                        note.isEmpty() ? null : note
                );
                return;
            }

            MainActivity.getDatabase().tasksDAO().add(
                    title, categoryId,
                    startDate, startTime,
                    cbInput.isChecked() ? 1 : 0,
                    isNotify ? 1 : 0,
                    note.isEmpty() ? null : note
            );

            updatePref();
        }).start();

        if (startDate != null && isNotify) {
            long taskDateTime = startDate + (startTime == null ? 0 : startTime);

            if (System.currentTimeMillis() < taskDateTime) {
                postAlarm(startDate+(startTime == null ? 0 : startTime));

                String msg = "Notify you at " +
                        TimeUtils.toDateString(startDate) + " " +
                        TimeUtils.toTimeNotation(startTime/1000);

                Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        setResult(RESULT_OK);
        finish();
    }

    private void exitPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Exit Task Setting")
                .setMessage("Do you want to save these settings?")
                .setPositiveButton("YES", (dialog, button) -> saveTask())
                .setNegativeButton("NO", (dialog, button) -> finish());

        builder.show();
    }

    private void setPropertyEnableStyle(boolean isEnable, ImageView iv, TextView tv, int iconRes) {
        iv.setImageResource(iconRes);

        if (isEnable) {
            iv.setImageTintList(ColorStateList.valueOf(Color.BLACK));
            tv.setTextColor(Color.BLACK);
        }
        else {
            iv.setImageTintList(ColorStateList.valueOf(Color.GRAY));
            tv.setTextColor(Color.GRAY);
        }
    }

    private void handleOnClickNotify(boolean notify) {
        isNotify = notify;
        ImageView ivNotify = findViewById(R.id.iv_notify);

        setPropertyEnableStyle(isNotify, ivNotify, findViewById(R.id.tv_notify_title),
                isNotify ? R.drawable.ic_notification_fill : R.drawable.ic_notification);

        TextView tvNotify = findViewById(R.id.tv_notify_desc);
        tvNotify.setVisibility(isNotify ? View.VISIBLE :View.GONE);

        // ask permission if the user enable notify
        if (isNotify && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Require Permission")
                    .setMessage("Cannot notify you properly")
                    .setPositiveButton("GRANT", (dialog, which) -> showNotificationSetting())
                    .setNegativeButton("NOT NOW", null);

            builder.show();
        }
    }

    private void showNotificationSetting() {
        Intent settingsIntent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(settingsIntent);
    }

    private void showDatePicker() {
        Calendar curr = Calendar.getInstance();

        DatePickerDialog datePicker = new DatePickerDialog(this, (v, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                TimeUtils.setMidNight(calendar); // set to 00:00:00

                startDate = calendar.getTimeInMillis();

                TextView tvDateDesc = findViewById(R.id.tv_date_desc);
                tvDateDesc.setText(TimeUtils.toDateString(calendar.getTimeInMillis()));

                setPropertyEnableStyle(true, findViewById(R.id.iv_date),
                        findViewById(R.id.tv_date_title), R.drawable.ic_calendar_fill);
        }, curr.get(Calendar.YEAR), curr.get(Calendar.MONTH), curr.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void showTimePicker() {
        Calendar curr= Calendar.getInstance();

        TimePickerDialog timePicker = new TimePickerDialog(this, (v, hr, min) -> {
                startTime = (int) TimeUnit.MINUTES.toMillis(hr * 60L + min);

                TextView tvTimeDesc = findViewById(R.id.tv_time_desc);
                tvTimeDesc.setText(TimeUtils.toTimeString(startTime));

                setPropertyEnableStyle(true, findViewById(R.id.iv_time),
                        findViewById(R.id.tv_time_title), R.drawable.ic_time_fill);
        }, curr.get(Calendar.HOUR_OF_DAY), curr.get(Calendar.MINUTE), true);

        timePicker.show();
    }

    private void loadExistedTask() {
        new Thread(() -> {
            int taskId = getIntent().getExtras().getInt("taskId");
            Task task = MainActivity.getDatabase().tasksDAO().getById(taskId);

            runOnUiThread(() -> {
                ((EditText) findViewById(R.id.et_task_name)).setText(task.title);

                startDate = task.startDate;
                startTime = task.startTime;

                if (task.startDate != null) {
                    setPropertyEnableStyle(true, findViewById(R.id.iv_date),
                            findViewById(R.id.tv_date_title), R.drawable.ic_calendar_fill);

                    TextView tvEndDate = findViewById(R.id.tv_date_desc);
                    tvEndDate.setText(TimeUtils.toDateString(task.startDate));
                }

                if (task.startTime != null) {
                    setPropertyEnableStyle(true, findViewById(R.id.iv_time),
                            findViewById(R.id.tv_time_title), R.drawable.ic_time_fill);

                    TextView tvTimeDesc = findViewById(R.id.tv_time_desc);
                    tvTimeDesc.setText(TimeUtils.toTimeString(startTime));
                }

                // isDone will not be null, which is set default to 0
                ((CheckBox) findViewById(R.id.cb_input)).setChecked(task.isDone == 1);

                // isNotify will not be null, which is set default to 0
                handleOnClickNotify(task.isNotify == 1);

                if (task.note != null) ((EditText) findViewById(R.id.et_note)).setText(task.note);
            });


            if (task.category != null) {
                Category category = MainActivity.getDatabase().categoryDAO().getById(task.category);
                runOnUiThread(() -> {
                    ((TextView) findViewById(R.id.atv_category)).setText(category.name);
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        exitPage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());

        findViewById(R.id.ib_exit).setOnClickListener(v -> exitPage());

        LayoutUtils.setAutoCategory(findViewById(R.id.atv_category));

        findViewById(R.id.ll_color_picker).setOnClickListener(v -> {
            new ColorDialog().show(getSupportFragmentManager(), null);
        });

        setCategoryTextChangeListener();
        setPickCategoryColorListener();

        findViewById(R.id.ll_time).setOnClickListener(v -> showTimePicker());
        findViewById(R.id.ll_date).setOnClickListener(v -> showDatePicker());

        findViewById(R.id.ll_notify).setOnClickListener(v -> handleOnClickNotify(!isNotify));

        if (getIntent().hasExtra("taskId")) loadExistedTask();
    }
}