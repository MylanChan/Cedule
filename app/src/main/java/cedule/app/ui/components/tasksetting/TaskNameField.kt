package cedule.app.ui.components.tasksetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.viewmodels.TaskEditViewModel

@Composable
fun TaskNameField(modifier: Modifier = Modifier) {
    val editVM = hiltViewModel<TaskEditViewModel>()
    val textValue by remember { derivedStateOf { editVM.title } }

    Box(modifier.padding(end=12.dp)) {
        BasicTextField(
            textValue,
            onValueChange = { newValue -> editVM.title = newValue },
            singleLine = true,
            textStyle = TextStyle(MaterialTheme.colorScheme.onSurface),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                innerTextField()

                if (textValue.isEmpty())
                    Text("Untitled Task", color= MaterialTheme.colorScheme.onSurfaceVariant)
            }
        )

        // bottom border
        Box(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                .align(Alignment.BottomCenter)
        )
    }
}
