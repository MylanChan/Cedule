package cedule.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.adapters.TaskAdapter;
import cedule.app.data.Database;
import cedule.app.data.Tasks;
import cedule.app.dialogs.AddTaskDialog;

public class TaskFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        RecyclerView rvTasks = view.findViewById(R.id.rv_tasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvTasks.setAdapter(new TaskAdapter((MainActivity) requireActivity()));

        view.findViewById(R.id.btn_add).setOnClickListener(v -> {
            new AddTaskDialog().show(getParentFragmentManager(), null);
        });
        return view;
    }
}
