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
    public Integer id;

    public Integer category;

    @NonNull
    public Integer createdDate;
    public Integer deadline;

    @ColumnInfo(defaultValue = "1")
    public Integer severity;

    @NonNull
    public String title;

    public String message;

    @ColumnInfo(defaultValue = "0")
    public Integer isCompleted;
}
