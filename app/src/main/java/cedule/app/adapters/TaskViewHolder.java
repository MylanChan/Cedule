package cedule.app.adapters;

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
import cedule.app.data.Tasks;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    final View view;

    private void toggleStatus(Boolean isChecked) {
        TextView tvTitle = view.findViewById(R.id.tv_title);
        tvTitle.setPaintFlags(isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0);

        TextView tvMsg = view.findViewById(R.id.tv_msg);
        tvMsg.setPaintFlags(isChecked ? Paint.STRIKE_THRU_TEXT_FLAG : 0);
    }

    public void loadData(Tasks tasks) {
        ((TextView) view.findViewById(R.id.tv_title))
                .setText(tasks.title);

        ((TextView) view.findViewById(R.id.tv_msg))
                .setText(tasks.message);
    }

    public TaskViewHolder(@NonNull View view) {
        super(view);
        this.view = view;

        CheckBox checkBox = view.findViewById(R.id.cb_input);

        view.findViewById(R.id.cl_item).setOnClickListener(v -> {
            checkBox.setChecked(!checkBox.isChecked());
        });

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> toggleStatus(isChecked));

        toggleStatus(checkBox.isChecked());
    }
}
