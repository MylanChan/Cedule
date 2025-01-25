package cedule.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun CeduleTheme(content: @Composable () -> Unit) {
    val colorScheme = when (isSystemInDarkTheme()) {
        true -> darkColorScheme()
        false -> lightColorScheme()
    }

    val systemUiController = rememberSystemUiController()
    systemUiController.setNavigationBarColor(colorScheme.surfaceBright)

    MaterialTheme(colorScheme, content = content)
}