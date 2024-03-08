package cedule.app.data;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TasksDAO {
    @Query("SELECT * FROM tasks")
    List<Tasks> getAllTasks();
}
