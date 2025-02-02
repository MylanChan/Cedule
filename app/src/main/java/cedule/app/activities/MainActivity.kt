package cedule.app.activities;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cedule.app.R
import cedule.app.ui.components.IconBox
import cedule.app.ui.components.page.FocusPage
import cedule.app.ui.components.page.HomePage
import cedule.app.ui.theme.CeduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            enableEdgeToEdge(
                navigationBarStyle = SystemBarStyle.auto(
                    MaterialTheme.colorScheme.surfaceBright.toArgb(),
                    MaterialTheme.colorScheme.surfaceBright.toArgb()
                )
            )
            val navController = rememberNavController()
            CeduleTheme {
                val sysUiController = rememberSystemUiController()
                sysUiController.setNavigationBarColor(MaterialTheme.colorScheme.surfaceContainer)

                Column {
                    NavHost(
                        navController,
                        Screen.Home.route,
                        Modifier.weight(1f),
                        enterTransition = { EnterTransition.None },
                        exitTransition = { ExitTransition.None}
                    ) {
                        composable(Screen.Home.route) { HomePage(navController) }
                        composable(Screen.Focus.route) { FocusPage(navController) }
                    }
                }
            }
        }
    }
}

sealed class Screen(val route: String, @DrawableRes val active: Int, @DrawableRes val inactive: Int) {
    data object Home : Screen("Home", R.drawable.ic_home_fill, R.drawable.ic_home)
    data object Focus : Screen("Focus", R.drawable.ic_focus_fill, R.drawable.ic_focus)
}

@Composable
fun NavBar(
    navController: NavController,
    layerColor: Color,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry.value?.destination?.route

    Box(
        modifier
            .background(layerColor)
            .height(65.dp)
            .fillMaxWidth()
    )
    BottomAppBar(
        modifier
            .navigationBarsPadding()
            .height(65.dp)
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        cutoutShape = CircleShape,
        elevation = 22.dp
    ) {
        listOf(Screen.Home, Screen.Focus).forEach {
            IconBox(
                iconRes =
                    if (currentRoute == it.route)
                        it.active
                    else
                        it.inactive,
                "Switch the page",
                Modifier.width(120.dp),
                tint =
                    if (currentRoute == it.route)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.outline
            ) {
                navController.navigate(it.route) {
                    popUpTo(navController.graph.startDestinationRoute ?: "") {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }

}