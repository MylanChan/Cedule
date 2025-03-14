package cedule.app.data.entities;

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["category"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.SET_DEFAULT
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "createTime", defaultValue = "CURRENT_TIMESTAMP")
    val createTime: Int? = null,

    @ColumnInfo(name = "title")
    val title: String? = null,

    @ColumnInfo(name = "category")
    var category: Int? = null,

    @ColumnInfo(name = "startDate")
    val startDate: Long? = null,

    @ColumnInfo(name = "startTime")
    val startTime: Int? = null,

    @ColumnInfo(name = "isDone", defaultValue = "0")
    val isDone: Boolean = false,

    @ColumnInfo(name = "isNotify", defaultValue = "0")
    val isNotify: Boolean = false,

    @ColumnInfo(name = "note")
    val note: String? = null
)