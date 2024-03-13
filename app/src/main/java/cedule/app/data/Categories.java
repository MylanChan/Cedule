package cedule.app.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Categories {
    @PrimaryKey
    public Integer id;

    @NonNull
    public String name;
}
