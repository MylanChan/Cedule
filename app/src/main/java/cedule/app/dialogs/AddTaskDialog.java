package cedule.app.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.adapters.TaskAdapter;
import cedule.app.data.Database;
import cedule.app.data.Tasks;

public class AddTaskDialog extends BottomSheetDialogFragment {
    private void handleOnClickSubmitBtn() {
        new Thread(() -> {
            Database database = MainActivity.getDatabase();

            TextView tvTitle = requireView().findViewById(R.id.tv_title);

            // avoid empty title
            if (tvTitle.getText().toString().length() == 0) {
                dismiss();
                return;
            }

            database.tasksDAO().addTask(null, null, null, tvTitle.getText().toString(), null);

            List<Tasks> tasksList = database.tasksDAO().getLastTask();
            if (tasksList.size() > 0) {
                requireActivity().runOnUiThread(() -> {
                    RecyclerView rvTasks = requireActivity().findViewById(R.id.rv_tasks);
                    TaskAdapter adapter = (TaskAdapter) rvTasks.getAdapter();
                    adapter.addTask(tasksList.get(0));
                });
            }
            dismiss();
        }).start();
    }

    @Override @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_task, container, false);

        view.findViewById(R.id.btn_submit).setOnClickListener(v -> handleOnClickSubmitBtn());

        return view;
    }
}
