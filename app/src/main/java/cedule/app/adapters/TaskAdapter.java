package cedule.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.data.entities.Task;

public class TaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Task> tasksList;
    private final List<Integer> selectedTasks = new ArrayList<>();

    private final AppCompatActivity activity;
    private boolean isSelectMode = false;

    private void setNormalModeStyles() {
        activity.findViewById(R.id.ll_ribbon_normal).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.ll_ribbon_select).setVisibility(View.GONE);
        activity.findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
    }

    private void setSelectModeStyles() {
        activity.findViewById(R.id.ll_ribbon_normal).setVisibility(View.GONE);
        activity.findViewById(R.id.ll_ribbon_select).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.btn_add).setVisibility(View.GONE);
    }

    public void discardSelectedTasks() {
        List<Task> discardTasks = new ArrayList<>();

        for (int adapterPos : selectedTasks) {
            discardTasks.add(tasksList.get(adapterPos));
        }

        tasksList.removeAll(discardTasks);

        if (tasksList.size() == 0) {
            activity.findViewById(R.id.iv_task_completed).setVisibility(View.VISIBLE);
        }

        selectedTasks.clear();

        notifyDataSetChanged();
        setNormalModeStyles();

        new Thread(() -> {
            List<Integer> idList = new ArrayList<>();
            for (Task task : discardTasks) {
                idList.add(task.id);
            }
            MainActivity.getDatabase().tasksDAO().discard(idList);
        }).start();
    }

    public boolean getIsSelectMode() {
        return isSelectMode;
    }

    public void setMode(boolean isSelectMode) {
        this.isSelectMode = isSelectMode;

        if (!isSelectMode) {
            setNormalModeStyles();
        }
        else {
            setSelectModeStyles();
        }
    }

    public void selectTask(Integer adapterPosition) {
        selectedTasks.add(adapterPosition);
        if (isSelectMode) setMode(true);
    }

    public void unselectTask(Integer adapterPosition) {
        selectedTasks.remove(adapterPosition);
        if (selectedTasks.size() == 0) setMode(false);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        ((TaskViewHolder) holder).init();
    }

//    @Override
//    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
//        super.onDetachedFromRecyclerView(recyclerView);
//
//        // some elements are shared with different Tabs
//        // and they may be hid if adapter is in select mode
//        activity.findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
//        activity.findViewById(R.id.ll_ribbon_normal).setVisibility(View.VISIBLE);
//        activity.findViewById(R.id.ll_ribbon_select).setVisibility(View.GONE);
//    }

    @Override @NonNull
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.holder_task_view, parent, false);

        return new TaskViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((TaskViewHolder) holder).loadData(activity, tasksList.get(position));
    }

    @Override
    public int getItemCount() {
        return tasksList == null ? 0 : tasksList.size();
    }

    public void addTask(Task task) {
        tasksList.add(task);

        notifyItemChanged(tasksList.size()-1);
        activity.findViewById(R.id.iv_task_completed).setVisibility(View.INVISIBLE);
    }

    public TaskAdapter(AppCompatActivity activity, List<Task> tasks) {
        this.activity = activity;
        tasksList = tasks;

        if (tasksList.size() == 0) {
            activity.findViewById(R.id.iv_task_completed).setVisibility(View.VISIBLE);
            return;
        }
        activity.findViewById(R.id.iv_task_completed).setVisibility(View.INVISIBLE);
    }
}
