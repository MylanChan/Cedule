package cedule.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import cedule.app.R;
import cedule.app.adapters.TaskAdapter;
import cedule.app.data.Database;
import cedule.app.data.dao.TaskDAO;
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

    private void initOnTabSelectedListener() {
        TabLayout tlTasks = findViewById(R.id.tl_tasks);

        tlTasks.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                RecyclerView rvTasks = findViewById(R.id.rv_tasks);

                switch (tab.getPosition()) {
                    case 0: {
                        loadAllTasks();
                        return;
                    }
                    default: {
                        CharSequence tabText = tab.getText();
                        if (tabText != null) getTasksByCategory(tabText.toString().toLowerCase());
                    }
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
            List<Task> tasks = MainActivity.getDatabase().tasksDAO().getAllTasks();

            runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(this, tasks)));
        }).start();
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
        loadAllTasks();

        findViewById(R.id.btn_add).setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskSettingActivity.class);
            startActivityForResult(intent, 2);
        });


        initOnTabSelectedListener();

        findViewById(R.id.ib_focus).setOnClickListener(v -> showFocusPage());
        findViewById(R.id.ib_filter).setOnClickListener(v -> {
            new FilterDialog().show(getSupportFragmentManager(), null);
        });

        findViewById(R.id.ib_sort).setOnClickListener(this::handleOnClickSort);
        findViewById(R.id.ib_trash).setOnClickListener(v -> handleOnClickDiscard());

        getWindow().setNavigationBarColor(getResources().getColor(R.color.surface));
        LayoutUtils.setBarColor(getWindow());

        getSupportFragmentManager().setFragmentResultListener("filterTask", this, (requestKey, result) -> {
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
                        tasks = db.tasksDAO().getTaskByFilter(startDate, endDate);
                    }
                }
                else if (startDate == -1 || endDate == -1) {
                    tasks = db.tasksDAO().getTasksByCategory(category.id);
                }
                else {
                    tasks = db.tasksDAO().getTaskByFilter(category.id, startDate, endDate);
                }

                List<Task> finalTasks = tasks;
                runOnUiThread(() -> rvTasks.setAdapter(new TaskAdapter(this, finalTasks)));
            }).start();

        });
    }
}
