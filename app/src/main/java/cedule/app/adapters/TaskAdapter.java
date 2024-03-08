package cedule.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.data.Database;
import cedule.app.data.Tasks;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Tasks> tasksList = null;

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new TaskViewHolder(inflater.inflate(R.layout.viewholder_task, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TaskViewHolder) holder).loadData(tasksList.get(position));
    }

    @Override
    public int getItemCount() {
        return tasksList == null ? 0 : tasksList.size();
    }

    private void setTaskList(List<Tasks> tasks) {
        tasksList = tasks;
        notifyDataSetChanged();
    }

    public TaskAdapter(MainActivity activity) {
        Database database = activity.getDatabase();
        new Thread(() -> {
            List<Tasks> tasks = database.tasksDAO().getAllTasks();
            activity.runOnUiThread(() -> {
                setTaskList(tasks);
            });
        }).start();
    }
}
