package cedule.app.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks", foreignKeys = @ForeignKey(
        entity = Categories.class,
        parentColumns = "id",
        childColumns = "category",
        onUpdate = 5,
        onDelete = 4
))
public class Tasks {
    @PrimaryKey
    Integer id;

    Integer category;

    @NonNull
    Integer createdDate;
    Integer deadline;

    @ColumnInfo(defaultValue = "1")
    Integer severity;

    @NonNull
    String title;

    String message;

    @ColumnInfo(defaultValue = "0")
    Integer isCompleted;
}
