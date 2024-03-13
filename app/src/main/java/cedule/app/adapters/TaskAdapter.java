package cedule.app.adapters;

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
    private final List<Tasks> tasksList;
    private final List<Integer> selectedTasks = new ArrayList<>();
    private final View container;

    public final static int MODE_NORMAL = 0;
    public final static int MODE_SELECT = 1;
    private int mode = MODE_NORMAL;

    private void setNormalModeStyles() {
        container.findViewById(R.id.ll_ribbon_normal).setVisibility(View.VISIBLE);
        container.findViewById(R.id.ll_ribbon_select).setVisibility(View.GONE);
        container.findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
    }

    private void setSelectModeStyles() {
        container.findViewById(R.id.ll_ribbon_normal).setVisibility(View.GONE);
        container.findViewById(R.id.ll_ribbon_select).setVisibility(View.VISIBLE);
        container.findViewById(R.id.btn_add).setVisibility(View.GONE);
    }

    public void discardSelectedTasks() {
        List<Integer> idList = new ArrayList<>();
        for (int i : selectedTasks) {
            idList.add(tasksList.get(i).id);
            tasksList.remove(i);
            notifyItemRemoved(i);
        }
        selectedTasks.clear();

        setNormalModeStyles();

        new Thread(() -> {
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
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        // show the add button which may be gone if adapter is in selecting mode
        container.findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
    }

    @Override @NonNull
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_task, parent, false);

        return new TaskViewHolder(this, view);
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

    public TaskAdapter(View container, List<Tasks> tasks) {
        this.container = container;
        tasksList = tasks;
    }
}
