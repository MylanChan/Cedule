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

    @Query("SELECT * FROM tasks WHERE severity=:severity")
    List<Tasks> getTasksBySeverity(Integer severity);

    @Query("INSERT INTO tasks (category, deadline, severity, title, message) " +
            "VALUES (:cat, :deadline, :severity, :title, :message)")
    void addTask(Integer cat, Integer deadline, Integer severity, String title, String message);

    @Query("DELETE FROM tasks WHERE id=:id")
    void removeTask(Integer id);

    @Query("UPDATE tasks SET isCompleted=:isCompleted WHERE id=:id")
    void updateTaskStatus(Integer id, Integer isCompleted);

    @Query("SELECT * FROM tasks ORDER BY id DESC LIMIT 1")
    List<Tasks> getLastTask();
}
