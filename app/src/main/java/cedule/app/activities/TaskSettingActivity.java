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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import cedule.app.R;
import cedule.app.data.Categories;
import cedule.app.data.Tasks;
import cedule.app.utils.TimeUtils;

public class TaskSettingActivity extends AppCompatActivity {
    private boolean isNotify = false;
    private Long startDate = null;
    private Integer startTime = null;

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

                        if (categoryName != "") {
                            MainActivity.getDatabase().tasksDAO().addCategory(categoryName);
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

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));

        findViewById(R.id.ib_exit).setOnClickListener(v -> exitPage());

        findViewById(R.id.ll_time).setOnClickListener(v -> {
            if (startDate == null) {
                Toast toast = new Toast(this);
                toast.setText("You need to configure the date first.");
                toast.show();
                return;
            }

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                ((TextView) findViewById(R.id.tv_time_desc)).setText( selectedHour + ":" + selectedMinute);

                Calendar time = Calendar.getInstance();
                time.setTimeInMillis(0);
                time.set(Calendar.HOUR, selectedHour);
                time.set(Calendar.MINUTE, selectedMinute);
                startTime = (int) time.getTimeInMillis();
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

                            startDate = calendar.getTimeInMillis();
                            ((TextView) findViewById(R.id.tv_date_desc))
                                    .setText(TimeUtils.toDateString(calendar.getTimeInMillis()));
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

                        ((TextView) findViewById(R.id.tv_date_desc))
                            .setText(TimeUtils.toDateString(task.startDate));

                        if (task.startTime != null) {
                            startTime = task.startTime;
                            ((TextView) findViewById(R.id.tv_time_desc)).setText(TimeUtils.toTimeString(task.startTime));
                        }

                        if (task.isNotify != null) {
                            handleOnClickNotify(task.isNotify == 1);
                        }
                    }

                    if (task.note != null) ((EditText) findViewById(R.id.tv_note)).setText(task.note);
                });


                if (task.category != null) {
                    Categories category = MainActivity.getDatabase().tasksDAO().getCategoryById(task.category);
                    runOnUiThread(() -> {
                        ((TextView) findViewById(R.id.tv_category_desc)).setText(category.name);
                    });
                }
            }).start();
        }

        new Thread(() -> {
            AutoCompleteTextView atv_category = findViewById(R.id.tv_category_desc);

            Categories[] categories = MainActivity.getDatabase().tasksDAO().getAllCategory();
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