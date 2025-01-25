package cedule.app.data.dao;

import androidx.room.Dao;
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query;
import androidx.room.Transaction;

import cedule.app.data.entities.Category;
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface CategoryDAO {
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    fun getByName(name: String): Flow<Category?>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<Category?>

    @Query("SELECT * FROM categories WHERE name LIKE :name || '%'")
    fun getSimilar(name: String): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(category: Category)

    @Query("UPDATE categories SET name = :name, color = :color WHERE id = :id")
    suspend fun update(id: Int, name: String, color: Int)

    @Query("SELECT * FROM categories")
    fun getAll(): Flow<List<Category>>

    @Transaction
    suspend fun insertOrUpdate(name: String, color: Int) {
        val category = getByName(name).first()
        if (category != null) {
            update(category.id!!, name, color)
        } else {
            add(Category(name = name, color = color))
        }
    }
}