package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
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
import cedule.app.services.TaskNotifyService;
import cedule.app.utils.TimeUtils;

public class TaskSettingActivity extends AppCompatActivity {
    private boolean isNotify = false;
    private Long startDate = null;
    private Integer startTime = null;

    private int getCategoryId(String name) {
        // if the category name existed, this code line will be ignored
        MainActivity.getDatabase().categoryDAO().addCategory(name);

        return MainActivity.getDatabase().categoryDAO().getCategoryByName(name).id;
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
                        String categoryName =  ((TextView) findViewById(R.id.tv_category_desc)).getText().toString();

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

                    if (startDate != null && startTime != null) {
                        Intent intent = new Intent(getApplicationContext(), TaskNotifyService.class);
                        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 0, intent,
                                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        System.out.println(startDate);
                        System.out.println(startTime);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, startDate + startTime, pi);
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
                isNotify ? R.drawable.ic_notification_active : R.drawable.ic_notification);

        TextView tvNotify = findViewById(R.id.tv_notify_desc);
        if (isNotify)  tvNotify.setVisibility(View.VISIBLE);
        else tvNotify.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));

        findViewById(R.id.ib_exit).setOnClickListener(v -> exitPage());

        findViewById(R.id.ll_time).setOnClickListener(v -> {
            if (startDate == null) {
                Toast.makeText(this, "You need to configure the date first", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                ((TextView) findViewById(R.id.tv_time_desc)).setText( selectedHour + ":" + selectedMinute);

                startTime = (int) (TimeUnit.HOURS.toMillis(selectedHour) + TimeUnit.MINUTES.toMillis(selectedMinute));
                setPropertyEnableStyle(true, findViewById(R.id.iv_time), findViewById(R.id.tv_time_title), R.drawable.ic_time);
            }, hour, minute, true);
            mTimePicker.show();
        });

        findViewById(R.id.ll_date).setOnClickListener(v -> {
                DatePickerDialog datepicker;

                datepicker = new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);

                            startDate = calendar.getTimeInMillis();
                            ((TextView) findViewById(R.id.tv_date_desc))
                                    .setText(TimeUtils.toDateString(calendar.getTimeInMillis()));

                            setPropertyEnableStyle(true, findViewById(R.id.iv_date),
                                    findViewById(R.id.tv_date_title), R.drawable.ic_calendar);
                        },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));


                datepicker.show();

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
                                findViewById(R.id.tv_date_title), R.drawable.ic_calendar);

                        ((TextView) findViewById(R.id.tv_date_desc))
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
                    Category category = MainActivity.getDatabase().categoryDAO().getCategoryById(task.category);
                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.tv_category_desc)).setText(category.name);
                    });
                }
            }).start();
        }

        new Thread(() -> {
            AutoCompleteTextView atv_category = findViewById(R.id.tv_category_desc);

            Category[] categories = MainActivity.getDatabase().categoryDAO().getAllCategory();
            String[] categoryNames = new String[categories.length];

            for (int i=0; i < categories.length; i++) {
                categoryNames[i] = categories[i].name;
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryNames);
                atv_category.setAdapter(adapter);
            });
        }).start();
    }
}