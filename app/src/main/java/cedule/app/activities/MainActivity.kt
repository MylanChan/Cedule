package cedule.app.activities;

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.R
import cedule.app.ui.components.CreateTaskFAB
import cedule.app.ui.components.IconBox
import cedule.app.ui.components.page.HomePage
import cedule.app.ui.theme.CeduleTheme
import cedule.app.viewmodels.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CeduleTheme {
                var route by remember { mutableStateOf<Screen>(Screen.Home) }

                Scaffold(
                    topBar = { TopAppBar() },
                    floatingActionButton = { CreateTaskFAB() },
                    isFloatingActionButtonDocked = true,
                    bottomBar = {
                        Box(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow).height(65.dp).fillMaxWidth())
                        androidx.compose.material.BottomAppBar(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .height(65.dp)
                                .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
                            cutoutShape = CircleShape,
                            backgroundColor = MaterialTheme.colorScheme.surfaceBright,
                            elevation = 22.dp
                        ) {
                            listOf(Screen.Home, Screen.Focus).forEach { screen ->
                                IconBox(screen.icon, "Switch the page", Modifier.width(120.dp)) { route = screen }
                            }
                        }
                    },

                ) { innerPadding ->
                    Column(
                        Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (route) {
                            Screen.Home -> {
                                HomePage(Modifier.weight(1f).fillMaxSize())
                            }
                            Screen.Focus -> {

                            }
                        }

                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(modifier: Modifier = Modifier) {
    val taskVM = hiltViewModel<TaskViewModel>()
    val todayTaskCount by taskVM.todayTaskCount.collectAsState(0)

    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(top = 24.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Welcome Back!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        if (todayTaskCount > 0)
            Text("$todayTaskCount tasks to be completed today", style = MaterialTheme.typography.bodyMedium)
        else
            Text("You done all the tasks today! ദ്ദി ˉ͈̀꒳ˉ͈́ )✧")
        HorizontalDivider(Modifier.padding(vertical = 12.dp))
    }
}



sealed class Screen(val title: String?, @DrawableRes val icon: Int) {
    data object Home : Screen("PickUp", R.drawable.ic_calendar)
    data object Focus : Screen("Profile", R.drawable.ic_focus)
}