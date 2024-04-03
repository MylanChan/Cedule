package cedule.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import cedule.app.R;
import cedule.app.data.Categories;
import cedule.app.data.Tasks;

public class TaskSettingActivity extends AppCompatActivity {
    private boolean isNotify = false;
    private Integer startDate = null;
    private Integer startTime = null;

    private void exitPage() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Task Setting")
                .setMessage("Do you want to save these settings?")
                .setPositiveButton("YES", (dialog, button) -> {
                    finish();

                    new Thread(() -> {
                        String title = ((EditText) findViewById(R.id.tv_task_name)).getText().toString();
                        if (title.equals("")) title = "Untitled Task";

                        Integer categoryId = null;


                        String categoryName =  ((TextView) findViewById(R.id.tv_category_desc)).getText().toString();
                        MainActivity.getDatabase().tasksDAO().addCategory(categoryName);

                        if (categoryName != "") {
                            categoryId = MainActivity.getDatabase().tasksDAO().getCategoryByName(categoryName).id;
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

    private void handleOnClickNotify(boolean notify) {
        isNotify = notify;

        ImageView ivNotify = findViewById(R.id.iv_notify);
        TextView tvNotify = findViewById(R.id.tv_notify_desc);

        if (isNotify)  {
            ivNotify.setImageResource(R.drawable.ic_notification_active);
            ivNotify.setImageTintList(ColorStateList.valueOf(Color.BLACK));

            tvNotify.setVisibility(View.VISIBLE);
            return;
        }
        ivNotify.setImageResource(R.drawable.ic_notification);
        ivNotify.setImageTintList(ColorStateList.valueOf(Color.GRAY));

        tvNotify.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_setting);

        findViewById(R.id.ib_exit).setOnClickListener(v -> exitPage());

        findViewById(R.id.ll_time).setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                ((TextView) findViewById(R.id.tv_time_desc)).setText( selectedHour + ":" + selectedMinute);
                startTime = (selectedHour * 60 + selectedMinute) * 1000;
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
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

                            startDate = (int) calendar.getTimeInMillis();
                        },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

                datepicker.show();
        });

        findViewById(R.id.ll_notify).setOnClickListener(v -> handleOnClickNotify(!isNotify));

        if (getIntent().hasExtra("taskId")) {
            new Thread(() -> {
                Tasks task = MainActivity.getDatabase().tasksDAO().getTaskById(getIntent().getExtras().getInt("taskId"));

                runOnUiThread(() -> {
                    ((EditText) findViewById(R.id.tv_task_name)).setText(task.title);

                    if (task.isDone != null) {
                        ((CheckBox) findViewById(R.id.cb_input)).setChecked(task.isDone == 1);
                    }

                    if (task.startDate != null) {
                        startDate = task.startDate;
                        ((TextView) findViewById(R.id.tv_date_desc)).setText(String.valueOf(task.startDate));

                        if (task.startTime != null) {
                            startTime = task.startTime;
                            ((TextView) findViewById(R.id.tv_time_desc)).setText(String.valueOf(task.startTime));
                        }

                        if (task.isNotify != null) {
                            handleOnClickNotify(task.isNotify == 1);
                        }
                    }

                    if (task.note != null) ((EditText) findViewById(R.id.tv_note)).setText(task.note);
                });


                if (task.category != null) {
                    Categories category = MainActivity.getDatabase().tasksDAO().getCategoryById(task.id);
                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.tv_category_desc)).setText(category.name);
                    });
                }
            }).start();
        }
    }
}