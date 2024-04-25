package cedule.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import cedule.app.data.Database;
import cedule.app.data.entities.Category;
import cedule.app.data.entities.Task;
import cedule.app.dialogs.FilterDialog;
import cedule.app.utils.LayoutUtils;

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
                    tasks = MainActivity.getDatabase().tasksDAO().getInNameAsc();
                }
                else if (itemId == R.id.item_sort_za) {
                    tasks = MainActivity.getDatabase().tasksDAO().getInNameDesc();
                }
                else if (itemId == R.id.item_sort_deadline) {
                    tasks = MainActivity.getDatabase().tasksDAO().getInDeadline();
                }
                else if (itemId == R.id.item_sort_default) { // creation time
                    tasks = MainActivity.getDatabase().tasksDAO().getAll();
                }

                List<Task> finalTasks = tasks;
                runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(this, finalTasks)));
            }).start();

            return true;
        });

        menu.show();
    }
    private void handleOnClickDiscard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Discard all selected tasks?")
                .setPositiveButton("Discard", (dialog, which) -> {
                    RecyclerView rvTasks = findViewById(R.id.rv_tasks);
                    TaskAdapter adapter = (TaskAdapter) rvTasks.getAdapter();

                    if (adapter != null) adapter.discardSelectedTasks();
                })
                .setNegativeButton("Cancel", null);

        builder.show();
    }

    private void showFocusPage() {
        Intent intent = new Intent(this, FocusActivity.class);
        startActivity(intent);
    }

    private void getTasksByCategory(String name) {
        new Thread(() -> {
            RecyclerView rvTasks = findViewById(R.id.rv_tasks);
            Category category = MainActivity.getDatabase().categoryDAO().getByName(name);

            if (category != null) {
                List<Task> tasks = MainActivity.getDatabase().tasksDAO().getByCategory(category.id);
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

    private void setFilterTaskListener() {
        getSupportFragmentManager().setFragmentResultListener("filterTask", this, (key, result) -> {
            TabLayout tlTasks = findViewById(R.id.tl_tasks);
            tlTasks.getTabAt(0).select();

            new Thread(() -> {
                Database db = MainActivity.getDatabase();

                String categoryName = result.getString("category");

                long startDate = result.getLong("startDate", -1);
                long endDate = result.getLong("endDate", -1);

                List<Task> tasks = null;

                Category category = db.categoryDAO().getByName(categoryName);
                if (categoryName == null || category == null) {
                    if (startDate != -1 && endDate != -1) {
                        tasks = db.tasksDAO().getByFilter(startDate, endDate);
                    }
                }
                else if (startDate == -1 || endDate == -1) {
                    tasks = db.tasksDAO().getByCategory(category.id);
                }
                else {
                    tasks = db.tasksDAO().getByFilter(category.id, startDate, endDate);
                }

                if (tasks == null) return;

                List<Task> finalTasks = tasks; // final variable for lambda function
                runOnUiThread(() -> {
                    RecyclerView rvTasks = findViewById(R.id.rv_tasks);
                    rvTasks.setAdapter(new TaskAdapter(this, finalTasks));
                });
            }).start();

        });
    }

    private void setOnTabSelectedListener() {
        TabLayout tlTasks = findViewById(R.id.tl_tasks);

        tlTasks.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    loadAllTasks();
                    return;
                }

                CharSequence tabText = tab.getText();
                if (tabText != null) {
                    getTasksByCategory(tabText.toString().toLowerCase());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void loadAllTasks() {
        RecyclerView rvTasks = findViewById(R.id.rv_tasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));

        new Thread(() -> {
            List<Task> tasks = MainActivity.getDatabase().tasksDAO().getAll();

            runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(this, tasks)));
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) loadAllTasks();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        RecyclerView rvTasks = findViewById(R.id.rv_tasks);
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        loadAllTasks();

        findViewById(R.id.btn_add).setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskSettingActivity.class);
            startActivityForResult(intent, 2);
        });

        setOnTabSelectedListener();

        findViewById(R.id.ib_focus).setOnClickListener(v -> showFocusPage());
        findViewById(R.id.ib_filter).setOnClickListener(v -> {
            new FilterDialog().show(getSupportFragmentManager(), null);
        });

        findViewById(R.id.ib_sort).setOnClickListener(this::handleOnClickSort);
        findViewById(R.id.ib_trash).setOnClickListener(v -> handleOnClickDiscard());

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());

        setFilterTaskListener();
    }
}
