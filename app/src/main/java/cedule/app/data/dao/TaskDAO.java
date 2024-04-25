package cedule.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import cedule.app.data.entities.Task;

@Dao
public interface TaskDAO {
    @Query("SELECT * FROM tasks")
    List<Task> getAll();

    @Query("SELECT * FROM tasks WHERE category=:category")
    List<Task> getByCategory(Integer category);

    @Query("UPDATE tasks SET title=:title, category=:cat, startDate=:startDate, startTime=:startTime, isDone=:isDone, isNotify=:isNotify, note=:note WHERE id=:taskId")
    void update(int taskId, String title, Integer cat, Long startDate, Integer startTime,
                Integer isDone, Integer isNotify, String note);

    @Query("INSERT INTO tasks (title, category, startDate, startTime, isDone, isNotify, note) " +
            "VALUES (:title, :cat, :startDate, :startTime, :isDone, :isNotify, :note)")
    void add(String title, Integer cat, Long startDate, Integer startTime,
             Integer isDone, Integer isNotify, String note);

    @Query("SELECT * FROM tasks WHERE id=:id LIMIT 1")
    Task getById(int id);

    @Query("DELETE FROM tasks WHERE id IN (:id)")
    void discard(List<Integer> id);

    @Query("UPDATE tasks SET isDone=:isDone WHERE id=:id")
    void updateStatus(Integer id, Integer isDone);

    @Query("SELECT * FROM tasks ORDER BY title DESC")
    List<Task> getInNameDesc();

    @Query("SELECT * FROM tasks ORDER BY startDate")
    List<Task> getInDeadline();

    @Query("SELECT * FROM tasks ORDER BY title")
    List<Task> getInNameAsc();

    @Query("SELECT * FROM tasks " +
            "WHERE category=:category AND startDate BETWEEN :startDate AND :endDate")
    List<Task> getByFilter(int category, long startDate, long endDate);

    @Query("SELECT * FROM tasks " +
            "WHERE startDate BETWEEN :startDate AND :endDate")
    List<Task> getByFilter(long startDate, long endDate);
}
