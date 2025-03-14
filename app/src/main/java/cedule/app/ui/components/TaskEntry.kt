package cedule.app.ui.components

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.activities.TaskSettingActivity
import cedule.app.data.entities.Task
import cedule.app.utils.TimeUtils
import cedule.app.viewmodels.TaskViewModel

@Composable
private fun TaskEntryDeadline(deadline: Long?, time: Int?, modifier: Modifier = Modifier) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        deadline?.let {
            Text(
                TimeUtils.toDateString(deadline),
                modifier,
                style = MaterialTheme.typography.labelMedium
            )
        }
        time?.let {
            Text(
                TimeUtils.toTimeNotation(time),
                modifier,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun TaskEntryName(task: Task, modifier: Modifier = Modifier) {
    val decoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None
    Text(
        task.title!!.ifBlank { "Untitled Task" },
        modifier,
        textDecoration = decoration,
        style = MaterialTheme.typography.bodyMedium
    )
}

private fun startTaskSettingActivity(from: Activity, taskId: Int) {
    val intent = Intent(from, TaskSettingActivity::class.java).apply {
        putExtra("id", taskId)
    }
    from.startActivity(intent)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskEntry(task: Task, modifier: Modifier = Modifier) {
    val activity = LocalContext.current as Activity

    val taskVM: TaskViewModel = hiltViewModel()
    val pendingDelete by taskVM.pendingDelete.collectAsState(emptyList())

    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .combinedClickable(
                onClick = {
                    if (pendingDelete.isNotEmpty()) {
                        if (pendingDelete.contains(task.id))
                            taskVM.pendingDelete.value -= task.id!!
                        else
                            taskVM.pendingDelete.value += task.id!!
                    }
                    else
                        startTaskSettingActivity(activity, task.id!!)
                },
                onLongClick = {
                    taskVM.pendingDelete.value += task.id!!
                }
            )
            .background(
                if (pendingDelete.contains(task.id))
                    MaterialTheme.colorScheme.surfaceContainerHigh
                else
                    Color.Transparent
            )
            .padding(4.dp),
        Arrangement.spacedBy(8.dp),
        Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isDone == 1,
            onCheckedChange = { taskVM.setDone(task.id!!, it) }
        )
        Column {
            TaskEntryName(task)
            TaskEntryDeadline(task.startDate, task.startTime)
        }
        CategoryBadge(task.category)
    }
}