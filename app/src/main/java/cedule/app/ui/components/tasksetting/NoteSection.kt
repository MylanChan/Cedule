package cedule.app.ui.components.tasksetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.viewmodels.TaskEditViewModel

@Composable
fun TaskNoteSection(modifier: Modifier = Modifier) {
    val editVM = hiltViewModel<TaskEditViewModel>()
    var textState by remember { mutableStateOf(TextFieldValue(editVM.note)) }

    HorizontalDivider(Modifier.padding(16.dp))

    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painterResource(cedule.app.R.drawable.ic_note),
            contentDescription = null,
            Modifier.size(24.dp)
        )
        Text(
            text = "Note",
            modifier = Modifier.padding(start = 12.dp, end = 24.dp),
        )
    }

    OutlinedTextField(
        value = textState,
        onValueChange = {
            textState = it
            editVM.note = it.text
        },
        Modifier
            .height(250.dp)
            .fillMaxWidth()
            .padding(12.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer),
        placeholder = { Text("Leave some messages to remind your future self...") },
        maxLines = 5,
    )
}