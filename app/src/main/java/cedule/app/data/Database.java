package cedule.app.data;

import androidx.room.RoomDatabase;

@androidx.room.Database(
        entities = {
                Categories.class,
                Tasks.class
        },
        version = 5
)
public abstract class Database extends RoomDatabase {
    public abstract TasksDAO tasksDAO();
}
