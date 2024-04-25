package cedule.app.data.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import cedule.app.data.entities.Category;

@Dao
public interface CategoryDAO {
    @Query("SELECT * FROM categories WHERE name=:name LIMIT 1")
    Category getByName(String name);

    @Query("SELECT * FROM categories WHERE id=:id LIMIT 1")
    Category getById(int id);

    @Query("INSERT INTO categories (name, color) VALUES (:name, :color)")
    void add(String name, Integer color);

    @Query("UPDATE categories SET name=:name, color=:color WHERE id=:id")
    void update(int id, String name, Integer color);

    @Query("SELECT * FROM categories")
    Category[] getAll();

    @Transaction
    default void insertUpdateCategory(String name, Integer color){
        Category category = getByName(name);
        if (category != null) {
            update(category.id, name, color);
        }
        else {
            add(name, color);
        }
    }
}
