package cedule.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import cedule.app.R;
import cedule.app.activities.FocusActivity;
import cedule.app.activities.MainActivity;
import cedule.app.activities.TaskSettingActivity;
import cedule.app.adapters.TaskAdapter;
import cedule.app.data.Categories;
import cedule.app.data.Tasks;
import cedule.app.dialogs.ConfirmationDialog;

public class TaskFragment extends Fragment {
    private View view;

    private void handleOnClickSort(View v) {
        PopupMenu menu = new PopupMenu(requireActivity(), v);
        menu.getMenuInflater().inflate(R.menu.menu_sort, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.item_sort_az) {

            }
            else if (itemId == R.id.item_sort_za) {

            }
            else if (itemId == R.id.item_sort_deadline) {

            }
            else if (itemId == R.id.item_sort_default) {
                // sort by id
            }
            return true;
        });

        menu.show();
    }
    private void handleOnClickDiscard() {
        RecyclerView rvTasks = view.findViewById(R.id.rv_tasks);

        new ConfirmationDialog("Discard all selected tasks?", (dialog) -> {
            TaskAdapter adapter = (TaskAdapter) rvTasks.getAdapter();

            if (adapter != null) adapter.discardSelectedTasks();
            dialog.dismiss();
        }).show(getParentFragmentManager(), null);
    }

    private void setTabWidth(int pos, float width) {
        TabLayout tlTasks = view.findViewById(R.id.tl_tasks);
        LinearLayout llTabItem0 = ((LinearLayout) ((LinearLayout) tlTasks.getChildAt(0)).getChildAt(pos));

        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) llTabItem0.getLayoutParams();

        layoutParams.weight = width;
        llTabItem0.setLayoutParams(layoutParams);
    }

    private void showFocusPage() {
        Intent intent = new Intent(requireActivity(), FocusActivity.class);
        startActivity(intent);

        // navigate back to overall page
        // focus page show as a Activity rather than a Fragment
        TabLayout.Tab tabItem1 = ((TabLayout) view.findViewById(R.id.tl_tasks)).getTabAt(1);
        if (tabItem1 != null) tabItem1.select();
    }

    private void getTasksByCategory(String name) {
        new Thread(() -> {
            RecyclerView rvTasks = view.findViewById(R.id.rv_tasks);
            List<Categories> category = MainActivity.getDatabase().tasksDAO().getCategoryIdByName(name);

            if (category.size() > 0) {
                List<Tasks> tasks = MainActivity.getDatabase().tasksDAO().getTasksByCategory(category.get(0).id);
                requireActivity().runOnUiThread(() -> {
                    rvTasks.setAdapter(new TaskAdapter(view, tasks));
                });
                return;
            }
            requireActivity().runOnUiThread(() -> {
                rvTasks.setAdapter(new TaskAdapter(view, new ArrayList<>()));
            });
        }).start();
    }

    private void initialOnTabSelectedListener() {
        TabLayout tlTasks = view.findViewById(R.id.tl_tasks);

        tlTasks.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                RecyclerView rvTasks = view.findViewById(R.id.rv_tasks);

                switch (tab.getPosition()) {
                    case 0: {
                        showFocusPage();
                        return;
                    }
                    case 1: {
                        new Thread(() -> {
                            List<Tasks> tasks = MainActivity.getDatabase().tasksDAO().getAllTasks();
                            requireActivity().runOnUiThread(() -> {
                                rvTasks.setAdapter(new TaskAdapter(view, tasks));
                            });
                        }).start();
                        return;
                    }
                    default: {
                        CharSequence tabText = tab.getText();
                        if (tabText != null) getTasksByCategory(tabText.toString());
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        view = inflater.inflate(R.layout.fragment_task, container, false);

        RecyclerView rvTasks = view.findViewById(R.id.rv_tasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(requireActivity()));

        new Thread(() -> {
            List<Tasks> tasks = MainActivity.getDatabase().tasksDAO().getAllTasks();

            requireActivity().runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(view, tasks)));
        }).start();

        view.findViewById(R.id.btn_add).setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), TaskSettingActivity.class);
            startActivity(intent);
        });

        TabLayout tlTasks = view.findViewById(R.id.tl_tasks);
        tlTasks.getTabAt(0).setIcon(R.drawable.ic_focus);
        tlTasks.getTabAt(1).select();

        setTabWidth(0, 0.5f);
        initialOnTabSelectedListener();

        view.findViewById(R.id.ib_sort).setOnClickListener(this::handleOnClickSort);
        view.findViewById(R.id.ib_trash).setOnClickListener(v -> handleOnClickDiscard());

        return view;
    }
}
