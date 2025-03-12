package cedule.app.ui.components.tasksetting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.R
import cedule.app.activities.ClearIconButton
import cedule.app.activities.TaskFieldIcon
import cedule.app.utils.TimeUtils
import cedule.app.viewmodels.TaskEditViewModel
import java.util.Calendar
import java.util.concurrent.TimeUnit

@Composable
fun TimeField() {
    val editVM = hiltViewModel<TaskEditViewModel>()
    var isModalShowed by remember { mutableStateOf(false) }

    val startTime by remember { derivedStateOf { editVM.startTime } }

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { isModalShowed = true }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskFieldIcon(startTime != null, R.drawable.ic_time_fill, R.drawable.ic_time)
        Text(
            "Time",
            Modifier.padding(start = 12.dp, end = 24.dp),
            color =
                if (startTime != null)
                    MaterialTheme.colorScheme.onSurface
                else
                    MaterialTheme.colorScheme.outline
        )

        startTime?.let {
            Text(
                text = TimeUtils.toTimeNotation(startTime!!),
                Modifier.weight(1f),
                textAlign = TextAlign.End
            )

            ClearIconButton(Modifier.padding(start=8.dp)) {
                editVM.startTime = null
            }
        }
    }

    if (isModalShowed) {
        TimePickerDialog(startTime, { isModalShowed = false }, {
            isModalShowed = false
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    time: Int?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val editVM = hiltViewModel<TaskEditViewModel>()
    val currentTime = Calendar.getInstance()

    time?.let {
        currentTime.timeInMillis = TimeUnit.MINUTES.toMillis(time - 8*60L)
    }

    val state = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = { DismissButton { onDismiss() } },
        confirmButton = {
            ConfirmButton {
                editVM.startTime = state.hour*60 + state.minute
                onConfirm()
            }
        },
        text = { TimePicker(state) }
    )
}

@Composable
private fun ConfirmButton(onConfirm: () -> Unit) {
    TextButton(
        onClick = onConfirm,
        content = { Text("OK") }
    )
}

@Composable
private fun DismissButton(onDismiss: () -> Unit) {
    TextButton(
        onClick = onDismiss,
        content = { Text("Cancel") }
    )
}