package cedule.app.data.entities;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories")
public class Category {
    @PrimaryKey
    public Integer id;

    @NonNull
    public String name;

    public Integer color;
}
