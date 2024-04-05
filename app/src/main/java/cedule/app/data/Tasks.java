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

    public Integer createTime;

    public String title;

    public Integer category;

    public Long startDate;

    public Integer startTime;

    @ColumnInfo(defaultValue = "0")
    public Integer isDone;

    public Integer isNotify;

    public String note;
}
