package cedule.app.activities;

import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.ui.components.tasksetting.AutoCompleteTextField
import cedule.app.ui.components.tasksetting.DateField
import cedule.app.ui.components.tasksetting.NotificationField
import cedule.app.ui.components.tasksetting.TaskNoteSection
import cedule.app.ui.components.tasksetting.TimeField
import cedule.app.ui.theme.CeduleTheme
import cedule.app.viewmodels.TaskEditViewModel
import cedule.app.viewmodels.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar


@AndroidEntryPoint
class TaskSettingActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val taskVM: TaskViewModel = hiltViewModel()
            val taskId = intent.getIntExtra("id", -1)
            val editVM: TaskEditViewModel = hiltViewModel()

            CeduleTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Task", textAlign = TextAlign.Center) },
                            Modifier.padding(horizontal = 12.dp),
                            navigationIcon = {
                                val context = LocalContext.current
                                IconButton(
                                    onClick = {
                                            editVM.saveTask {
                                                taskVM.saveTask(context, it)
                                            }
                                        finish()
                                    },
                                    Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        rememberVectorPainter(Icons.Filled.KeyboardArrowLeft),
                                        "Close this page"
                                    )
                                }

                            }
                        )
                    }
                ) { innerPadding ->

                    LaunchedEffect(Unit) {
                        taskVM.getTask(taskId).collect {
                            it?.let { editVM.loadTask(it) }
                        }
                    }

                    Column(Modifier.padding(innerPadding)) {
                        TaskSettingScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun TaskSettingScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(scrollState)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val editVM: TaskEditViewModel = hiltViewModel()

            val isDone by remember { derivedStateOf { editVM.isDone } }
            Checkbox(
                checked = isDone == 1,
                onCheckedChange = { editVM.isDone = if (it) 1 else 0 }
            )

            val textValue by remember { derivedStateOf { editVM.title } }

            BasicTextField(
                textValue,
                onValueChange = { newValue -> editVM.title = newValue },
                Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .padding(end = 8.dp),
                singleLine = true,
                textStyle = TextStyle(color = Color.Black),
                decorationBox = { innerTextField ->
                    innerTextField()

                    if (textValue.isEmpty())
                        Text("Untitled Task", color=MaterialTheme.colorScheme.onSurfaceVariant)
                }
            )
            AutoCompleteTextField(Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        DateField()
        TimeField()
        NotificationField()
        TaskNoteSection()
    }
}

@Composable
fun ClearIconButton(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    Box(
        modifier
            .size(24.dp)
            .clickable(onClick = onClick),
        Alignment.CenterEnd
    ) {
        Icon(
            rememberVectorPainter(Icons.Default.Clear),
            "Clear this field's data",
            tint = tint
        )
    }
}

@Composable
fun TaskFieldIcon(
    isActive: Boolean,
    @DrawableRes active: Int,
    @DrawableRes inactive: Int,
    modifier: Modifier = Modifier
) {
    Icon(
        painterResource(if (isActive) active else inactive),
        contentDescription = null,
        modifier.size(24.dp),
        tint =
            if (isActive)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.outline
    )
}