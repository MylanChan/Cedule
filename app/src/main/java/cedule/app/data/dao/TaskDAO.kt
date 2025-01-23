package cedule.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.*
import cedule.app.data.entities.Category
import cedule.app.data.entities.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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

    @Query("SELECT * FROM tasks ORDER BY title DESC")
    fun getInNameDesc(): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY startDate")
    fun getInDeadline(): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY title")
    fun getInNameAsc(): Flow<List<Task>>

    @Query("SELECT * FROM tasks " +
            "WHERE category=:category AND startDate BETWEEN :startDate AND :endDate")
    fun getByFilter(category: Int, startDate: Long, endDate: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks " +
            "WHERE startDate BETWEEN :startDate AND :endDate")
    fun getByFilter(startDate: Long, endDate: Long): Flow<List<Task>>

    @Transaction
    suspend fun insertOrUpdate(task: Task) {
        if (task.id != null) update(task) else add(task)
    }

    @Query("SELECT * FROM tasks ORDER BY createTime DESC LIMIT 1")
    suspend fun getLatestTask(): Task?
}