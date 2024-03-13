package cedule.app.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.data.Tasks;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Tasks> tasksList = null;
    private static final List<Integer> selectedTasks = new ArrayList<>();

    public final static int MODE_NORMAL = 0;
    public final static int MODE_SELECT = 1;
    private static int mode = MODE_NORMAL;

    public static void setMode(int modeNumber) {
        mode = modeNumber;
    }
    public static int getMode() {
        return mode;
    }

    public static void selectTask(Integer adapterPosition) {
        selectedTasks.add(adapterPosition);
        if (getMode() == MODE_NORMAL) setMode(MODE_SELECT);
    }

    public static void unselectTask(Integer adapterPosition) {
        selectedTasks.remove(adapterPosition);
        if (selectedTasks.size() == 0) setMode(MODE_NORMAL);
    }

    @Override @NonNull
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_task, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TaskViewHolder) holder).loadData(tasksList.get(position));
    }

    @Override
    public int getItemCount() {
        return tasksList == null ? 0 : tasksList.size();
    }

    public void addTask(Tasks task) {
        if (tasksList == null) return;
        tasksList.add(task);

        notifyItemChanged(tasksList.size()-1);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setTaskList(List<Tasks> tasks) {
        tasksList = tasks;
        notifyDataSetChanged();
    }

    public TaskAdapter(List<Tasks> tasks) {
        setTaskList(tasks);
    }
}
