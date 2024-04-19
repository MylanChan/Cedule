package cedule.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import cedule.app.data.entities.Task;

@Dao
public interface TaskDAO {
    @Query("SELECT * FROM tasks")
    List<Task> getAllTasks();

    @Query("SELECT * FROM tasks WHERE category=:category")
    List<Task> getTasksByCategory(Integer category);

    @Query("UPDATE tasks SET title=:title, category=:cat, startDate=:startDate, startTime=:startTime, isDone=:isDone, isNotify=:isNotify, note=:note WHERE id=:taskId")
    void updateTask(int taskId, String title, Integer cat, Long startDate, Integer startTime,
                    Integer isDone, Integer isNotify, String note);

    @Query("INSERT INTO tasks (title, category, startDate, startTime, isDone, isNotify, note) " +
            "VALUES (:title, :cat, :startDate, :startTime, :isDone, :isNotify, :note)")
    void addTask(String title, Integer cat, Long startDate, Integer startTime,
                 Integer isDone, Integer isNotify, String note);

    @Query("SELECT * FROM tasks WHERE id=:id LIMIT 1")
    Task getTaskById(int id);

    @Query("DELETE FROM tasks WHERE id IN (:id)")
    void discardTasks(List<Integer> id);

    @Query("UPDATE tasks SET isDone=:isDone WHERE id=:id")
    void updateTaskStatus(Integer id, Integer isDone);

    @Query("SELECT * FROM tasks ORDER BY id DESC LIMIT 1")
    List<Task> getLastTask();

    @Query("SELECT * FROM tasks ORDER BY title DESC")
    List<Task> getTasksInNameDesc();

    @Query("SELECT * FROM tasks ORDER BY startDate")
    List<Task> getTasksInDeadline();

    @Query("SELECT * FROM tasks ORDER BY title")
    List<Task> getTasksInNameAsc();

    @Query("SELECT * FROM tasks " +
            "WHERE category=:category AND startDate BETWEEN :startDate AND :endDate")
    List<Task> getTaskByFilter(int category, long startDate, long endDate);

    @Query("SELECT * FROM tasks " +
            "WHERE startDate BETWEEN :startDate AND :endDate")
    List<Task> getTaskByFilter(long startDate, long endDate);
}
