package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cedule.app.R;
import cedule.app.data.entities.Category;
import cedule.app.data.entities.Task;
import cedule.app.dialogs.ColorDialog;
import cedule.app.services.TaskNotifyService;
import cedule.app.utils.LayoutUtils;
import cedule.app.utils.TimeUtils;

public class TaskSettingActivity extends AppCompatActivity {
    private boolean isNotify = false;
    private Long startDate = null;
    private Integer startTime = null;

    private void postAlarm(long timestamp) {
        Intent intent = new Intent(getApplicationContext(), TaskNotifyService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pi);
    }

    private int getCategoryId(String name) {
        int color = findViewById(R.id.ll_color).getBackgroundTintList().getDefaultColor();
        MainActivity.getDatabase().categoryDAO().add(name.toLowerCase(), color);

        return MainActivity.getDatabase().categoryDAO().getByName(name).id;
    }

    private void exitPage() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Task Setting")
                .setMessage("Do you want to save these settings?")
                .setPositiveButton("YES", (dialog, button) -> {
                    setResult(RESULT_OK);
                    finish();

                    new Thread(() -> {
                        String title = ((EditText) findViewById(R.id.tv_task_name)).getText().toString();
                        if (title.equals("")) title = null;

                        Integer categoryId = null;
                        String categoryName =  ((TextView) findViewById(R.id.atv_category)).getText().toString();

                        // empty text field is empty string rather than null
                        if (!categoryName.equals("")) {
                            categoryId = getCategoryId(categoryName);
                        }

                        String note = ((EditText) findViewById(R.id.tv_note)).getText().toString();
                        if (getIntent().hasExtra("taskId")) {
                            MainActivity.getDatabase().tasksDAO().updateTask(
                                    getIntent().getExtras().getInt("taskId"),
                                    title,
                                    categoryId,
                                    startDate,
                                    startTime,
                                    ((CheckBox) findViewById(R.id.cb_input)).isChecked() ? 1 : 0,
                                    isNotify ? 1 : 0,
                                    note == "" ? null : note
                            );
                            return;
                        }
                        MainActivity.getDatabase().tasksDAO().addTask(
                                title,
                                categoryId,
                                startDate,
                                startTime,
                                ((CheckBox) findViewById(R.id.cb_input)).isChecked() ? 1 : 0,
                                isNotify ? 1 : 0,
                                note == "" ? null : note
                        );

                    }).start();

                    if (isNotify) {
                        System.out.println(startDate != null && System.currentTimeMillis() < startDate+(startTime == null ? 0 : startTime));
                        if (startDate != null && System.currentTimeMillis() < startDate+(startTime == null ? 0 : startTime)) {
                            postAlarm(startDate+(startTime == null ? 0 : startTime));

                            Toast.makeText(this, "Notify you at " + TimeUtils.toDateString(startDate) + " " + TimeUtils.toTimeNotation(startTime/1000), Toast.LENGTH_SHORT).show();
                        }
                        else if (startDate == null){
                            Calendar calendar = Calendar.getInstance();
                            TimeUtils.setMidNight(calendar);

                            calendar.add(Calendar.MILLISECOND, startTime);
                            if (System.currentTimeMillis() < calendar.getTimeInMillis()) {
                                calendar.add(Calendar.DATE, +1);
                            }

                            postAlarm(calendar.getTimeInMillis());
                            Toast.makeText(this, "Notify you at " +
                                    TimeUtils.toDateString(calendar.getTimeInMillis()) + " " +
                                    TimeUtils.toTimeNotation(
                                            (int) TimeUnit.MINUTES.toSeconds(calendar.get(Calendar.HOUR_OF_DAY) * 60
                                                    + calendar.get(Calendar.MINUTE))
                                    ), Toast.LENGTH_SHORT).show();
                        }
                    }

                    dialog.dismiss();
                })
                .setNegativeButton("NO", (dialog, button) -> {
                    finish();
                    dialog.dismiss();
                }).show();
    }

    @Override
    public void onBackPressed() {
        exitPage();
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

        setPropertyEnableStyle(isNotify, ivNotify,
                findViewById(R.id.tv_notify_title),
                isNotify ? R.drawable.ic_notification_fill : R.drawable.ic_notification);

        TextView tvNotify = findViewById(R.id.tv_notify_desc);
        if (isNotify)  tvNotify.setVisibility(View.VISIBLE);
        else tvNotify.setVisibility(View.GONE);

        // only ask permission when enabling notify
        // ignore when disabling notify
        if (!isNotify) return;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(this)
                    .setTitle("Require Permission")
                    .setMessage("Cannot notify you properly")
                    .setNegativeButton("NOT NOW", null)
                    .setPositiveButton("GRANT", (dialog, which) -> {
                        // open notification setting page
                        Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                        startActivity(settingsIntent);
                    })
                    .show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());

        findViewById(R.id.ib_exit).setOnClickListener(v -> exitPage());

        findViewById(R.id.ll_time).setOnClickListener(v -> {
            new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                    startTime = (int) (TimeUnit.MINUTES.toMillis(selectedHour*60L+selectedMinute));

                    TextView tvTimeDesc = findViewById(R.id.tv_time_desc);
                    tvTimeDesc.setText(TimeUtils.toTimeString(startTime));

                    setPropertyEnableStyle(true, findViewById(R.id.iv_time),
                            findViewById(R.id.tv_time_title), R.drawable.ic_time_fill);
                },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE), true)
                .show();
        });

        findViewById(R.id.ll_date).setOnClickListener(v -> {
            new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        TimeUtils.setMidNight(calendar);

                        startDate = calendar.getTimeInMillis();
                        ((TextView) findViewById(R.id.tv_endDate))
                                .setText(TimeUtils.toDateString(calendar.getTimeInMillis()));

                        setPropertyEnableStyle(true, findViewById(R.id.iv_date),
                                findViewById(R.id.tv_startDate), R.drawable.ic_calendar_fill);
                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                    .show();
        });

        findViewById(R.id.ll_notify).setOnClickListener(v -> handleOnClickNotify(!isNotify));

        // determine whether editing a task or creating a task
        if (getIntent().hasExtra("taskId")) {
            new Thread(() -> {
                Task task = MainActivity.getDatabase().tasksDAO().getTaskById(getIntent().getExtras().getInt("taskId"));

                runOnUiThread(() -> {
                    ((EditText) findViewById(R.id.tv_task_name)).setText(task.title);

                    if (task.isDone != null) {
                        ((CheckBox) findViewById(R.id.cb_input)).setChecked(task.isDone == 1);
                    }

                    if (task.startDate != null) {
                        startDate = task.startDate;

                        setPropertyEnableStyle(true, findViewById(R.id.iv_date),
                                findViewById(R.id.tv_startDate), R.drawable.ic_calendar);

                        ((TextView) findViewById(R.id.tv_endDate))
                            .setText(TimeUtils.toDateString(task.startDate));

                        if (task.startTime != null) {
                            startTime = task.startTime;

                            setPropertyEnableStyle(true, findViewById(R.id.iv_time),
                                    findViewById(R.id.tv_time_title), R.drawable.ic_time);

                            ((TextView) findViewById(R.id.tv_time_desc))
                                .setText(TimeUtils.toTimeString(startTime));
                        }

                        if (task.isNotify != null) {
                            handleOnClickNotify(task.isNotify == 1);
                        }
                    }

                    if (task.note != null) ((EditText) findViewById(R.id.tv_note)).setText(task.note);
                });


                if (task.category != null) {
                    Category category = MainActivity.getDatabase().categoryDAO().getById(task.category);
                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.atv_category)).setText(category.name);
                    });
                }
            }).start();
        }

        findViewById(R.id.ll_color_picker).setOnClickListener(v -> {
            new ColorDialog().show(getSupportFragmentManager(), null);
        });

        LayoutUtils.setAutoCategory(findViewById(R.id.atv_category));

        ((AutoCompleteTextView) findViewById(R.id.atv_category)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String text = ((AutoCompleteTextView) findViewById(R.id.atv_category)).getText().toString();

                if (text.length() >= 1) {
                    findViewById(R.id.ll_color_picker).setVisibility(View.VISIBLE);
                    new Thread(() -> {
                        Category category = MainActivity.getDatabase().categoryDAO().getByName(text);
                        if (category != null) {
                            findViewById(R.id.ll_color).setBackgroundTintList(ColorStateList.valueOf(category.color));
                        }
                        else {
                            findViewById(R.id.ll_color).setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                        }
                    }).start();
                }
                else {
                    findViewById(R.id.ll_color_picker).setVisibility(View.GONE);
                }
            }
        });

        getSupportFragmentManager().setFragmentResultListener("pickColor", this, (key, result) -> {
            if (result.containsKey("color")) {
                int color = result.getInt("color", Color.RED);
                findViewById(R.id.ll_color).setBackgroundTintList(ColorStateList.valueOf(color));
            }
        });
    }
}