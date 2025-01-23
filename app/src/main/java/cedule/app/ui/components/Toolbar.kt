package cedule.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.R
import cedule.app.viewmodels.Sort
import cedule.app.viewmodels.TaskViewModel

@Composable
private fun SortButton(modifier: Modifier = Modifier) {
    val taskVM: TaskViewModel = hiltViewModel()
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        IconBox(R.drawable.ic_sort, "Sort the task list", modifier) {
            isExpanded = true
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("by name in ASC") } ,
                onClick = {
                    isExpanded = false
                    taskVM.setOrder(Sort.SORT_BY_NAME_ASC)
                }
            )
            DropdownMenuItem(
                text = { Text("by name in DESC") } ,
                onClick = {
                    isExpanded = false
                    taskVM.setOrder(Sort.SORT_BY_NAME_DESC)
                }
            )
            DropdownMenuItem(
                text = { Text("by deadline") } ,
                onClick = {
                    isExpanded = false
                    taskVM.setOrder(Sort.SORT_BY_DEADLINE)
                }
            )
        }
    }
}

@Composable
fun Toolbar(modifier: Modifier = Modifier) {
    Row(
        modifier
            .height(48.dp)
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        Arrangement.End,
        Alignment.CenterVertically
    ) {
        val taskVM = hiltViewModel<TaskViewModel>()
        val pendingDelete by taskVM.pendingDelete.collectAsState(emptyList())

        if (pendingDelete.isNotEmpty()) {
            val context = LocalContext.current
            IconBox(R.drawable.ic_trash, "Delete the selected tasks") {
                taskVM.discard(context, pendingDelete)
            }
        }
        else {
            SortButton()
        }
    }
}
