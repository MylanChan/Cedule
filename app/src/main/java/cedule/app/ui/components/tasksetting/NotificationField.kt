package cedule.app.ui.components.tasksetting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.R
import cedule.app.activities.TaskFieldIcon
import cedule.app.viewmodels.TaskEditViewModel


@Composable
fun NotificationField(modifier: Modifier = Modifier) {
    val editVM = hiltViewModel<TaskEditViewModel>()
    val isNotify by remember { derivedStateOf { editVM.isNotify == 1 } }

    Row(
        modifier
            .fillMaxWidth()
            .clickable { editVM.isNotify = if (isNotify) 0 else 1 }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskFieldIcon(isNotify, R.drawable.ic_notification_fill, R.drawable.ic_notification)
        Text(
            "Notify",
            Modifier.padding(start = 12.dp, end = 24.dp),
            if (isNotify)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.outline
        )

        if (isNotify)
            Text("on time", Modifier.weight(1f), textAlign = TextAlign.End)
    }
}