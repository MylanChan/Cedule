package cedule.app.adapters;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.data.Tasks;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    private MainActivity activity;
    final View view;

    final private int STATUS_NORMAL = 0;
    final private int STATUS_SELECTED = 1;
    private int status = STATUS_NORMAL;

    private Tasks task;
    private Tasks getTask() {
        return task;
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

    private Boolean handleOnLongClickItem(View view) {
        // long click -> select the item or cancel its selection
        if (status == STATUS_NORMAL) {
            view.setBackgroundColor(Color.parseColor("#AAE6E6E6"));
            status = STATUS_SELECTED;
        }
        else if (status == STATUS_SELECTED) {
            // transparent background
            view.setBackgroundColor(Color.parseColor("#00000000"));
            status = STATUS_NORMAL;
        }
        return true;
    }

    private void handleOnClickItem() {
        CheckBox checkBox = view.findViewById(R.id.cb_input);
        checkBox.setChecked(!checkBox.isChecked());

        new Thread(() -> {
            activity.getDatabase().tasksDAO()
                    .updateTaskStatus(getTask().id, checkBox.isChecked() ? 1 : 0);
        }).start();
    }

    public TaskViewHolder(MainActivity activity, View view) {
        super(view);
        this.activity = activity;
        this.view = view;

        view.findViewById(R.id.cl_item)
                .setOnClickListener(v -> handleOnClickItem());

        view.findViewById(R.id.cl_item)
                .setOnLongClickListener(this::handleOnLongClickItem);

        CheckBox checkBox = view.findViewById(R.id.cb_input);
        checkBox.setOnCheckedChangeListener((v, isChecked) -> toggleStatus(isChecked));

        toggleStatus(checkBox.isChecked());
    }
}
