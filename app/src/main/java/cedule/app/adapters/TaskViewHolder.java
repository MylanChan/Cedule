package cedule.app.adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.data.Tasks;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    public final View view;
    private final TaskAdapter adapter;

    public static final int STATUS_NORMAL = 0;
    public static final int STATUS_SELECTED = 1;
    public int status = STATUS_NORMAL;

    private Tasks task;
    private Tasks getTask() {
        return task;
    }

    private void updateIsChecked() {
        CheckBox checkBox = view.findViewById(R.id.cb_input);
        checkBox.setChecked(!checkBox.isChecked());

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

    public void loadData(Tasks task) {
        this.task = task;

        CheckBox cbInput = view.findViewById(R.id.cb_input);
        cbInput.setChecked(task.isCompleted != null && task.isCompleted == 1);

        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setText(task.title);

        TextView tvMsg = view.findViewById(R.id.tv_msg);
        if (task.message == null) {
            tvMsg.setVisibility(View.GONE);
            return;
        }
        tvMsg.setText(task.message);
    }

    private void handleOnClickItem() {
        if (adapter.getMode() == TaskAdapter.MODE_NORMAL) {
            updateIsChecked();
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
