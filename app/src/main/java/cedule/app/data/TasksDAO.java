package cedule.app.data;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TasksDAO {
    @Query("SELECT * FROM tasks")
    List<Tasks> getAllTasks();

    @Query("SELECT * FROM tasks WHERE category=:category")
    List<Tasks> getTasksByCategory(Integer category);

    @Query("UPDATE tasks SET title=:title, category=:cat, startDate=:startDate, startTime=:startTime, isDone=:isDone, isNotify=:isNotify, note=:note WHERE id=:taskId")
    void updateTask(int taskId, String title, Integer cat, Integer startDate, Integer startTime,
                    Integer isDone, Integer isNotify, String note);

    @Query("INSERT INTO tasks (title, category, startDate, startTime, isDone, isNotify, note) " +
            "VALUES (:title, :cat, :startDate, :startTime, :isDone, :isNotify, :note)")
    void addTask(String title, Integer cat, Integer startDate, Integer startTime,
                 Integer isDone, Integer isNotify, String note);

    @Query("SELECT * FROM tasks WHERE id=:id LIMIT 1")
    Tasks getTaskById(int id);

    @Query("DELETE FROM tasks WHERE id IN (:id)")
    void discardTasks(List<Integer> id);

    @Query("UPDATE tasks SET isDone=:isDone WHERE id=:id")
    void updateTaskStatus(Integer id, Integer isDone);

    @Query("SELECT * FROM tasks ORDER BY id DESC LIMIT 1")
    List<Tasks> getLastTask();

    @Query("SELECT * FROM categories WHERE name=:name LIMIT 1")
    Categories getCategoryByName(String name);

    @Query("SELECT * FROM categories WHERE id=:id LIMIT 1")
    Categories getCategoryById(int id);

    @Query("INSERT OR IGNORE INTO categories (name) VALUES (:name)")
    void addCategory(String name);
}
