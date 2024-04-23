package cedule.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;

import cedule.app.data.entities.Category;

@Dao
public interface CategoryDAO {
    @Query("SELECT * FROM categories WHERE name=:name LIMIT 1")
    Category getByName(String name);

    @Query("SELECT * FROM categories WHERE id=:id LIMIT 1")
    Category getById(int id);

    @Query("INSERT INTO categories (name, color) VALUES (:name, :color)")
    void add(String name, Integer color);

    @Query("UPDATE categories SET color=:color WHERE id=:id")
    void updateColor(int id, int color);

    @Query("SELECT * FROM categories")
    Category[] getAll();
}
