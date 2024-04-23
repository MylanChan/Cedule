package cedule.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import cedule.app.R;
import cedule.app.adapters.TaskAdapter;
import cedule.app.data.entities.Category;
import cedule.app.data.entities.Task;
import cedule.app.dialogs.FilterDialog;

public class TaskActivity extends AppCompatActivity {
    private void handleOnClickSort(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        menu.getMenuInflater().inflate(R.menu.menu_sort, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            new Thread(() -> {
                RecyclerView rvTasks = findViewById(R.id.rv_tasks);
                List<Task> tasks = null;

                int itemId = item.getItemId();

                if (itemId == R.id.item_sort_az) {
                    tasks = MainActivity.getDatabase().tasksDAO().getTasksInNameAsc();
                }
                else if (itemId == R.id.item_sort_za) {
                    tasks = MainActivity.getDatabase().tasksDAO().getTasksInNameDesc();
                }
                else if (itemId == R.id.item_sort_deadline) {
                    tasks = MainActivity.getDatabase().tasksDAO().getTasksInDeadline();
                }
                else if (itemId == R.id.item_sort_default) {
                    tasks = MainActivity.getDatabase().tasksDAO().getAllTasks();
                }

                List<Task> finalTasks = tasks;
                runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(this, finalTasks)));
            }).start();

            return true;
        });

        menu.show();
    }
    private void handleOnClickDiscard() {
        RecyclerView rvTasks = findViewById(R.id.rv_tasks);

        new AlertDialog.Builder(this)
                .setTitle("Discard all selected tasks?")
                .setPositiveButton("Discard", (dialog, which) -> {
                    TaskAdapter adapter = (TaskAdapter) rvTasks.getAdapter();

                    if (adapter != null) adapter.discardSelectedTasks();
                    dialog.dismiss();
                })

                .show();
    }

    private void setTabWidth(int pos, float width) {
        TabLayout tlTasks = findViewById(R.id.tl_tasks);
        LinearLayout llTabItem0 = ((LinearLayout) ((LinearLayout) tlTasks.getChildAt(0)).getChildAt(pos));

        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) llTabItem0.getLayoutParams();

        layoutParams.weight = width;
        llTabItem0.setLayoutParams(layoutParams);
    }

    private void showFocusPage() {
        Intent intent = new Intent(this, FocusActivity.class);
        startActivity(intent);

        // navigate back to overall page
        // focus page show as a Activity rather than a Fragment
        TabLayout.Tab tabItem1 = ((TabLayout) findViewById(R.id.tl_tasks)).getTabAt(1);
        if (tabItem1 != null) tabItem1.select();
    }

    private void getTasksByCategory(String name) {
        new Thread(() -> {
            RecyclerView rvTasks = findViewById(R.id.rv_tasks);
            Category category = MainActivity.getDatabase().categoryDAO().getByName(name);

            if (category != null) {
                List<Task> tasks = MainActivity.getDatabase().tasksDAO().getTasksByCategory(category.id);
                runOnUiThread(() -> {
                    rvTasks.setAdapter(new TaskAdapter(this, tasks));
                });
                return;
            }
            runOnUiThread(() -> {
                rvTasks.setAdapter(new TaskAdapter(this, new ArrayList<>()));
            });
        }).start();
    }

    private void initialOnTabSelectedListener() {
        TabLayout tlTasks = findViewById(R.id.tl_tasks);

        tlTasks.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                RecyclerView rvTasks = findViewById(R.id.rv_tasks);

                switch (tab.getPosition()) {
                    case 0: {
                        showFocusPage();
                        return;
                    }
                    case 1: {
                        new Thread(() -> {
                            List<Task> tasks = MainActivity.getDatabase().tasksDAO().getAllTasks();
                            runOnUiThread(() -> {
                                rvTasks.setAdapter(new TaskAdapter(TaskActivity.this, tasks));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            RecyclerView rvTasks = findViewById(R.id.rv_tasks);

            new Thread(() -> {
                List<Task> tasks = MainActivity.getDatabase().tasksDAO().getAllTasks();

                runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(this, tasks)));
            }).start();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        RecyclerView rvTasks = findViewById(R.id.rv_tasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<Task> tasks = MainActivity.getDatabase().tasksDAO().getAllTasks();

            runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(this, tasks)));
        }).start();

        findViewById(R.id.btn_add).setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskSettingActivity.class);
            startActivityForResult(intent, 2);
        });

        TabLayout tlTasks = findViewById(R.id.tl_tasks);
        tlTasks.getTabAt(0).setIcon(R.drawable.ic_focus);
        tlTasks.getTabAt(1).select();

        setTabWidth(0, 0.5f);
        initialOnTabSelectedListener();

        findViewById(R.id.ib_filter).setOnClickListener(v -> {
            new FilterDialog().show(getSupportFragmentManager(), null);
        });

        findViewById(R.id.ib_sort).setOnClickListener(this::handleOnClickSort);
        findViewById(R.id.ib_trash).setOnClickListener(v -> handleOnClickDiscard());

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));

        getSupportFragmentManager().setFragmentResultListener("filterTask", this, (requestKey, result) -> {
            new Thread(() -> {
                String category = result.getString("category");

                Long startDate = result.getLong("startDate", -1);
                Long endDate = result.getLong("endDate", -1);

                List<Task> tasks;

                if (category != null) {
                    MainActivity.getDatabase().categoryDAO().add(category, null);
                    int catId = MainActivity.getDatabase().categoryDAO().getByName(category).id;

                    if (startDate == -1 && endDate == -1) {
                        tasks = MainActivity.getDatabase().tasksDAO().getTaskByFilter(
                            catId, startDate, endDate
                        );
                    }
                    else {
                        tasks = MainActivity.getDatabase().tasksDAO().getTasksByCategory(catId);
                    }
                }
                else {
                    tasks = MainActivity.getDatabase().tasksDAO().getTaskByFilter(startDate, endDate);
                }


                List<Task> finalTasks = tasks;
                runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(this, finalTasks)));
            }).start();

        });
    }
}
