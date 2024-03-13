package cedule.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import cedule.app.R;
import cedule.app.activities.FocusActivity;
import cedule.app.activities.MainActivity;
import cedule.app.adapters.TaskAdapter;
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

        TabLayout tlTasks = view.findViewById(R.id.tl_tasks);
        tlTasks.getTabAt(0).setIcon(R.drawable.ic_focus);

        tlTasks.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    Intent intent = new Intent(requireActivity(), FocusActivity.class);
                    startActivity(intent);
                    ((TabLayout) view.findViewById(R.id.tl_tasks)).getTabAt(1).select();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        LinearLayout llTabItem0 = ((LinearLayout) ((LinearLayout) tlTasks.getChildAt(0)).getChildAt(0));

        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) llTabItem0.getLayoutParams();

        layoutParams.weight = 0.5f;
        llTabItem0.setLayoutParams(layoutParams);

        ((TabLayout) view.findViewById(R.id.tl_tasks)).getTabAt(1).select();

        return view;
    }
}
