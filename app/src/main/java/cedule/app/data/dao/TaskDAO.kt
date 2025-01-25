package cedule.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.*
import cedule.app.data.entities.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {
    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE category=:category")
    fun getByCategory(category: Int): Flow<List<Task>>

    @Update
    suspend fun update(task: Task)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(task: Task)

    @Query("SELECT * FROM tasks WHERE id=:id LIMIT 1")
    fun getById(id: Int): Flow<Task>

    @Query("DELETE FROM tasks WHERE id IN (:id)")
    suspend fun discard(id: List<Int>)

    @Query("UPDATE tasks SET isDone=:isDone WHERE id=:id")
    suspend fun updateStatus(id: Int, isDone: Int)

    @Query("SELECT * FROM tasks WHERE startDate == :startDate")
    fun getByDate(startDate: Long): Flow<List<Task>>

    @Transaction
    suspend fun insertOrUpdate(task: Task) {
        if (task.id != null) update(task) else add(task)
    }

    @Query("SELECT * FROM tasks ORDER BY createTime DESC LIMIT 1")
    suspend fun getLatestTask(): Task?

    @Query("SELECT COUNT(*) FROM tasks WHERE startDate=:date AND isDone=0")
    fun countTasks(date: Long): Flow<Int>
}