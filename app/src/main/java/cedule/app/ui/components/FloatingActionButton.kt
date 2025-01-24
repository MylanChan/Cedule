package cedule.app.ui.components

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cedule.app.activities.TaskSettingActivity

private fun startActivity(from: Activity, to: Class<out ComponentActivity>) {
    from.startActivity(Intent(from, to))
}

@Composable
fun CreateTaskFAB(modifier: Modifier = Modifier) {
    val context = LocalContext.current as Activity

    FloatingActionButton(
        onClick = { startActivity(context, TaskSettingActivity::class.java) },
        modifier,
        CircleShape
    ) {
        Icon(Icons.Default.Add, "Create a new task")
    }
}