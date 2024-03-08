package cedule.app.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Categories {
    @PrimaryKey
    Integer id;

    @NonNull
    String name;
}
