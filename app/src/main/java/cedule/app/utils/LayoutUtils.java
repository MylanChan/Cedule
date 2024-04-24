package cedule.app.utils;

import android.view.Window;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class LayoutUtils {
    public static void setBarColor(Window window) {
        WindowInsetsControllerCompat windowController =
                WindowCompat.getInsetsController(window, window.getDecorView());

        windowController.setAppearanceLightStatusBars(true);
        windowController.setAppearanceLightNavigationBars(true);
    }
}
