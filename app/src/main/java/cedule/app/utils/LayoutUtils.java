package cedule.app.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import cedule.app.R;
import cedule.app.activities.MainActivity;
import cedule.app.data.entities.Category;

public class LayoutUtils {
    public static void setBarColor(Window window) {
        WindowInsetsControllerCompat windowController =
                WindowCompat.getInsetsController(window, window.getDecorView());

        windowController.setAppearanceLightStatusBars(true);
        windowController.setAppearanceLightNavigationBars(true);
    }

    public static void setAutoCategory(AutoCompleteTextView atvCategory) {
        new Thread(() -> {
            Category[] categories = MainActivity.getDatabase().categoryDAO().getAll();
            String[] categoryNames = new String[categories.length];

            for (int i=0; i < categories.length; i++) {
                categoryNames[i] = categories[i].name;
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(atvCategory.getContext(),
                        android.R.layout.simple_dropdown_item_1line, categoryNames);

                atvCategory.setAdapter(adapter);
            });
        }).start();
    }
}
