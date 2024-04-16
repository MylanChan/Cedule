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
    private final List<TaskViewHolder> viewHolders = new ArrayList<>();

    private final AppCompatActivity activity;

    public final static int MODE_NORMAL = 0;
    public final static int MODE_SELECT = 1;
    private int mode = MODE_NORMAL;

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
            MainActivity.getDatabase().tasksDAO().discardTasks(idList);
        }).start();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int modeNumber) {
        mode = modeNumber;

        if (mode == MODE_NORMAL) {
            setNormalModeStyles();
        }
        else if (mode == MODE_SELECT) {
            setSelectModeStyles();
        }
    }

    public void selectTask(Integer adapterPosition) {
        selectedTasks.add(adapterPosition);
        if (mode == MODE_NORMAL) setMode(MODE_SELECT);
    }

    public void unselectTask(Integer adapterPosition) {
        selectedTasks.remove(adapterPosition);
        if (selectedTasks.size() == 0) setMode(MODE_NORMAL);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);

        // After discarding tasks, their ViewHolder will reuse later on if has new task
        // They are in select mode before the discard
        // Thus, it needs to change back the status and style same as normal mode
        ((TaskViewHolder) holder).initialize();
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        // some elements are shared with different Tabs
        // and they may be hid if adapter is in select mode
        activity.findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.ll_ribbon_normal).setVisibility(View.VISIBLE);
        activity.findViewById(R.id.ll_ribbon_select).setVisibility(View.GONE);
    }

    @Override @NonNull
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.holder_task_view, parent, false);

        TaskViewHolder viewHolder = new TaskViewHolder(this, view);
        viewHolders.add(viewHolder);
        return viewHolder;
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
