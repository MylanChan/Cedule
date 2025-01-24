package cedule.app.ui.components.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.ui.components.DateWheel
import cedule.app.ui.components.TaskEntry
import cedule.app.ui.components.Toolbar
import cedule.app.viewmodels.TaskViewModel

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    Column(
        modifier.background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateWheel()

        Surface(
            Modifier
                .padding(top = 12.dp)
                .weight(1f)
                .fillMaxWidth(),
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(30.dp, 30.dp)
        ) {
            Column(
                Modifier.background(
                    MaterialTheme.colorScheme.surfaceContainerLow,
                    RoundedCornerShape(30.dp, 30.dp)
                )
            ) {
                Toolbar(Modifier.padding(top = 12.dp))
                TaskList(Modifier.weight(1f))
            }
        }
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