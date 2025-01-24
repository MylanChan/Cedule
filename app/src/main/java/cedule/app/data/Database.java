package cedule.app.data;

import androidx.room.AutoMigration;
import androidx.room.RoomDatabase;

import cedule.app.data.dao.CategoryDAO;
import cedule.app.data.dao.TaskDAO;
import cedule.app.data.entities.Category;
import cedule.app.data.entities.Task;

@androidx.room.Database(
        entities = { Category.class, Task.class  },
        version = 1
)
public abstract class Database extends RoomDatabase {
    public abstract TaskDAO tasksDAO();

    public abstract CategoryDAO categoryDAO();
}
