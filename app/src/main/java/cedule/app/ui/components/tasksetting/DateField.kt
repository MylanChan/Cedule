package cedule.app.ui.components.tasksetting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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

@Composable
fun DateField() {
    val editVM = hiltViewModel<TaskEditViewModel>()
    var isModalShowed by remember { mutableStateOf(false) }

    val deadline by remember { derivedStateOf { editVM.startDate } }

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { isModalShowed = true }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TaskFieldIcon(deadline != null, R.drawable.ic_calendar_fill, R.drawable.ic_calendar)
        Text("Date", Modifier.padding(start = 12.dp, end = 24.dp))

        deadline?.let {
            Text(
                text = TimeUtils.toDateString(deadline!!),
                Modifier.weight(1f),
                textAlign = TextAlign.End
            )

            ClearIconButton(Modifier.padding(start=8.dp)) {
                editVM.startDate = null
            }
        }
    }

    if (isModalShowed) {
        DatePickerDialog(deadline, { editVM.startDate = it }) {
            isModalShowed = false
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    date: Long? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val state = rememberDatePickerState(date)

    DatePickerDialog(
        modifier = Modifier.padding(12.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            DateDialogTextButton("OK") {
                state.selectedDateMillis?.let {
                    val calendar = Calendar.getInstance()
                    val offsetInMillis = calendar.timeZone.getOffset(it)
                    onDateSelected(it-offsetInMillis)
                }

                onDismiss()
            }
        },
        dismissButton = { DateDialogTextButton("Cancel", onDismiss) }
    ) {
        DatePicker(state = state)
    }
}

@Composable
private fun DateDialogTextButton(text: String, onClick: () -> Unit) {
    TextButton(onClick=onClick) {
        Text(text)
    }
}