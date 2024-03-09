package cedule.app.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.data.Database;
import cedule.app.data.Tasks;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final MainActivity activity;
    private List<Tasks> tasksList = null;

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_task, parent, false);

        return new TaskViewHolder(activity, view);
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

    public TaskAdapter(MainActivity activity) {
        this.activity = activity;

        new Thread(() -> {
            List<Tasks> tasks = activity.getDatabase().tasksDAO().getAllTasks();
            
            activity.runOnUiThread(() -> setTaskList(tasks));
        }).start();
    }
}
