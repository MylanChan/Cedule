package cedule.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import cedule.app.data.entities.Category;

@Dao
public interface CategoryDAO {
    @Query("SELECT * FROM categories WHERE name=:name LIMIT 1")
    Category getCategoryByName(String name);

    @Query("SELECT * FROM categories WHERE id=:id LIMIT 1")
    Category getCategoryById(int id);

    @Query("INSERT OR IGNORE INTO categories (name) VALUES (:name)")
    void addCategory(String name);

    @Query("SELECT * FROM categories")
    Category[] getAllCategory();
}
