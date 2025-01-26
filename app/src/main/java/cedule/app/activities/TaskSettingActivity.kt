package cedule.app.activities;

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.ui.components.tasksetting.AutoCompleteTextField
import cedule.app.ui.components.tasksetting.DateField
import cedule.app.ui.components.tasksetting.NotificationField
import cedule.app.ui.components.tasksetting.TaskNameField
import cedule.app.ui.components.tasksetting.TaskNoteSection
import cedule.app.ui.components.tasksetting.TimeField
import cedule.app.ui.theme.CeduleTheme
import cedule.app.viewmodels.CategoryViewModel
import cedule.app.viewmodels.TaskEditViewModel
import cedule.app.viewmodels.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TaskSettingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val taskVM: TaskViewModel = hiltViewModel()
            val taskId = intent.getIntExtra("id", -1)
            val editVM: TaskEditViewModel = hiltViewModel()

            CeduleTheme {
                Scaffold(
                    bottomBar = { BottomBar() }
                ) { innerPadding ->

                    LaunchedEffect(Unit) {
                        taskVM.getTask(taskId).collect {
                            it?.let { editVM.loadTask(it) }
                        }
                    }

                    Column(
                        Modifier
                            .padding(innerPadding)
                            .padding(top=48.dp)
                    ) {
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

            TaskNameField(Modifier.weight(1.2f))
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

@Composable
fun ColorSelectionDialog(onDismiss: () -> Unit, onSelect: (Long) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select a color for category") },
        text = {
            Column {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                    ColorButton(0xFF7BCCF6, onClick = onSelect)
                    ColorButton(0xFF03D9C5, onClick = onSelect)
                    ColorButton(0xFFFDF7DC, onClick = onSelect)
                    ColorButton(0xFFADA5CD, onClick = onSelect)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun ColorButton(color: Long, onClick: (Long) -> Unit) {
    Box(
        Modifier
            .size(48.dp)
            .background(Color(color), CircleShape)
            .clip(CircleShape)
            .clickable { onClick(color) },
    )
}

@Composable
private fun BottomBar(modifier: Modifier = Modifier) {
    val taskVM: TaskViewModel = hiltViewModel()
    val editVM: TaskEditViewModel = hiltViewModel()
    val categoryVM: CategoryViewModel = hiltViewModel()

    val coroutineScope = CoroutineScope(Dispatchers.IO)
    val activity = LocalContext.current as Activity

    var isDialogShowed by remember { mutableStateOf(false) }

    if (isDialogShowed) {
        PermissionDialog(onDismiss = { isDialogShowed = false }) {
            val intent = Intent("android.settings.APP_NOTIFICATION_SETTINGS").apply {
                putExtra("android.provider.extra.APP_PACKAGE", activity.packageName)
            }
            activity.startActivity(intent)
        }
    }

    BottomAppBar(
        modifier
            .navigationBarsPadding()
            .height(65.dp)
            .clip(RoundedCornerShape(15.dp, 15.dp, 0.dp, 0.dp)),
        backgroundColor = MaterialTheme.colorScheme.surfaceBright,
        cutoutShape = CircleShape,
        elevation = 22.dp
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable { activity.finish() },
            contentAlignment = Alignment.Center
        ) {
            Text("Cancel")
        }

        Box(
            Modifier
                .fillMaxHeight()
                .width(1.dp)
                .padding(vertical = 12.dp)
                .background(MaterialTheme.colorScheme.outline)
        )

        Box(
            Modifier
                .fillMaxHeight()
                .weight(1f)
                .clickable {
                    if (editVM.isNotify == 1 && !checkNotificationPermission(activity)) {
                        isDialogShowed = true
                        return@clickable
                    }

                    if (editVM.categoryName.isNotBlank()) {
                        categoryVM.saveCategory(editVM.categoryName, editVM.categoryColor)

                        coroutineScope.launch {
                            categoryVM.getByName(editVM.categoryName)
                                .take(1)
                                .collect { category ->
                                    editVM.category = category?.id
                                    editVM.saveTask { taskVM.saveTask(activity, it) }
                                }
                        }
                    }
                    else
                        editVM.saveTask { taskVM.saveTask(activity, it) }

                    activity.finish()
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Save")
        }
    }
}

@Composable
fun PermissionDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Require permission") },
        text = { Text("to ensure you are notified on time") },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text("Grant")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun checkNotificationPermission(context: Context): Boolean {
    val p = ContextCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS")
    return p == PackageManager.PERMISSION_GRANTED
}