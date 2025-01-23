package cedule.app.data.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val id: Int? = null,
    val name: String,
    val color: Int? = null
)