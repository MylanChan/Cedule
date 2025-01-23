package cedule.app.activities;

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.R
import cedule.app.ui.components.IconBox
import cedule.app.ui.components.TaskEntry
import cedule.app.ui.components.Toolbar
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
                Scaffold(
                    topBar = { TopAppBar() },
                    floatingActionButton = { CreateTaskFAB() }
                ) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        Toolbar(Modifier.padding(top = 12.dp))
                        TaskList(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(modifier: Modifier = Modifier) {
    val activity = LocalContext.current as Activity

    Surface(shadowElevation = 4.dp) {
        Row(
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .statusBarsPadding()
                .padding(top = 8.dp)
        ) {
            IconBox(R.drawable.ic_focus, "Open focus page") {
                startActivity(activity, FocusActivity::class.java)
            }

            var selectedTabIndex by remember { mutableIntStateOf(0) }
            val tabTitles = listOf("Overall", "Work", "Study")

            TabRow(selectedTabIndex) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        text = { TabText(title, selectedTabIndex == index) },
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                    )
                }
            }
        }
    }
}

@Composable
private fun TabText(text: String, isSelected: Boolean, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier,
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )
}

private fun startActivity(from: Activity, to: Class<out ComponentActivity>) {
    from.startActivity(Intent(from, to))
}

@Composable
private fun CreateTaskFAB(modifier: Modifier = Modifier) {
    val context = LocalContext.current as Activity

    FloatingActionButton(
        onClick = { startActivity(context, TaskSettingActivity::class.java) },
        modifier
    ) {
        Icon(Icons.Default.Add, "Create a new task")
    }
}

@Composable
private fun TaskList(modifier: Modifier = Modifier) {
    val taskVM: TaskViewModel = hiltViewModel()
    val tasks by taskVM.tasks.collectAsState(initial = emptyList())

    if (tasks.isNotEmpty()) {
        LazyColumn(modifier.padding(horizontal = 12.dp)) {
            items(tasks) {
                TaskEntry(it, modifier.padding(vertical = 8.dp))
            }
        }
    }
    else {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ദ്ദി ˉ͈̀꒳ˉ͈́ )", style = MaterialTheme.typography.titleLarge)
                Text("You done all the tasks")
            }
        }
    }
}