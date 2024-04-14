package cedule.app.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.activities.TaskSettingActivity;
import cedule.app.data.Tasks;
import cedule.app.utils.TimeUtils;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    public final View view;
    private final TaskAdapter adapter;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SELECTED = 1;
    public int status = STATUS_NORMAL;

    private AppCompatActivity activity;

    private Tasks task;
    private Tasks getTask() {
        return task;
    }

    private void updateIsChecked() {
        CheckBox checkBox = view.findViewById(R.id.cb_input);
        checkBox.setChecked(checkBox.isChecked());

        new Thread(() -> {
            MainActivity.getDatabase().tasksDAO()
                    .updateTaskStatus(getTask().id, checkBox.isChecked() ? 1 : 0);
        }).start();
    }

    private void setNormalStyle() {
        view.setBackgroundColor(Color.parseColor("#00000000"));
        adapter.unselectTask(getAdapterPosition());
    }

    private void toggleSelection() {
        if (status == STATUS_NORMAL) {
            status = STATUS_SELECTED;
            view.setBackgroundColor(Color.parseColor("#AAE6E6E6"));
            adapter.selectTask(getAdapterPosition());
        }
        else if (status == STATUS_SELECTED) {
            status = STATUS_NORMAL;
            setNormalStyle();
        }
    }

    private void toggleStatus(Boolean isChecked) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setPaintFlags(isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0);

        TextView tvMsg = view.findViewById(R.id.tv_msg);
        tvMsg.setPaintFlags(isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
    }

    public void loadData(AppCompatActivity activity, Tasks task) {
        this.activity = activity;
        this.task = task;

        CheckBox cbInput = view.findViewById(R.id.cb_input);
        cbInput.setChecked(task.isDone != null && task.isDone == 1);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(task.title == null ? "Untitled Task" : task.title);

        TextView tvMsg = view.findViewById(R.id.tv_msg);

        if (task.startDate != null) {
            if (task.startTime != null) {
                tvMsg.setText(TimeUtils.toDateString(task.startDate) + " " + TimeUtils.toTimeString(task.startTime));
                return;
            }
            tvMsg.setText(TimeUtils.toDateString(task.startDate));
        }
        else {
            tvMsg.setVisibility(View.GONE);
        }
    }

    private void handleOnClickItem() {
        if (adapter.getMode() == TaskAdapter.MODE_NORMAL) {
            Intent intent = new Intent(activity, TaskSettingActivity.class);
            intent.putExtra("taskId", task.id);

            activity.startActivityForResult(intent, 1);
        }
        else if (adapter.getMode() == TaskAdapter.MODE_SELECT){
            toggleSelection();
        }
    }

    public void initialize() {
        status = STATUS_NORMAL;
        setNormalStyle();
    }

    public TaskViewHolder(TaskAdapter adapter, View view) {
        super(view);
        this.adapter = adapter;
        this.view = view;

        view.findViewById(R.id.cl_item)
                .setOnClickListener(v -> handleOnClickItem());

        view.findViewById(R.id.cb_input).setOnClickListener(v -> updateIsChecked());

        view.findViewById(R.id.cl_item).setOnLongClickListener(v -> {
                toggleSelection();
                return true;
        });

        CheckBox checkBox = view.findViewById(R.id.cb_input);
        checkBox.setOnCheckedChangeListener((v, isChecked) -> {
            if (adapter.getMode() == TaskAdapter.MODE_SELECT) {
                v.setChecked(!isChecked);
                return;
            }
            toggleStatus(isChecked);
        });

        toggleStatus(checkBox.isChecked());
    }
}
