package cedule.app.ui.components.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import cedule.app.activities.NavBar
import cedule.app.ui.components.CreateTaskFAB
import cedule.app.ui.components.DateWheel
import cedule.app.ui.components.TaskEntry
import cedule.app.ui.components.Toolbar
import cedule.app.utils.TimeUtils
import cedule.app.viewmodels.TaskViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun HomePage(navController: NavController, modifier: Modifier = Modifier) {
    val sysUiController = rememberSystemUiController()
    sysUiController.setStatusBarColor(MaterialTheme.colorScheme.surface)

    Scaffold(
        floatingActionButton = { CreateTaskFAB() },
        isFloatingActionButtonDocked = true,
        bottomBar = { NavBar(navController, MaterialTheme.colorScheme.surfaceContainerLow) }
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 48.dp),
            Arrangement.spacedBy(12.dp)
        ) {
            HomeTopBar()
            Column(
                Modifier.background(MaterialTheme.colorScheme.background),
                Arrangement.spacedBy(24.dp)
            ) {
                DateWheel()

                Surface(
                    Modifier.weight(1f),
                    shadowElevation = 8.dp,
                    shape = RoundedCornerShape(30.dp, 30.dp)
                ) {
                    Column(Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)) {
                        Toolbar(Modifier.padding(top = 12.dp))
                        TaskList(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


@Composable
private fun TaskList(modifier: Modifier = Modifier) {
    val taskVM: TaskViewModel = hiltViewModel()
    val tasks by taskVM.tasks.collectAsState(initial = emptyList())

    when (tasks.isNotEmpty()) {
        true -> {
            LazyColumn(modifier.padding(horizontal = 12.dp)) {
                items(tasks) {
                    TaskEntry(it, Modifier.padding(vertical = 8.dp))
                }
            }
        }
        else -> EmptyTaskList(modifier)
    }
}

@Composable
private fun EmptyTaskList(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("ദ്ദി ˉ͈̀꒳ˉ͈́ )", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
            Text("You done all the tasks", color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun HomeTopBar(modifier: Modifier = Modifier) {
    val taskVM = hiltViewModel<TaskViewModel>()
    val todayTaskCount by taskVM.todayTaskCount.collectAsState(0)

    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 36.dp, start = 16.dp, end = 16.dp),
        Arrangement.spacedBy(8.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            Arrangement.SpaceBetween,
            Alignment.Bottom
        ) {
            val weekdayFormat = SimpleDateFormat("EEEE", Locale.ENGLISH) // e.g. Monday
            Column {
                Text(
                    weekdayFormat.format(TimeUtils.getTodayMidnight()),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    TimeUtils.toDateString(System.currentTimeMillis()),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Text(
                text =
                if (todayTaskCount > 0)
                    "$todayTaskCount tasks left"
                else
                    "ദ്ദി ˉ͈̀꒳ˉ͈́ )✧",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))
    }
}