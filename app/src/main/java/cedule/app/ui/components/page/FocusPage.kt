package cedule.app.ui.components.page

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import cedule.app.R
import cedule.app.activities.NavBar
import cedule.app.utils.TimeUtils
import kotlinx.coroutines.delay

@Composable
fun FocusPage(navController: NavController, modifier: Modifier = Modifier) {
    var isCounting by remember { mutableStateOf(false) }
    var timeCount by remember { mutableIntStateOf(0) }
    LaunchedEffect(isCounting) {
        while (isCounting) {
            delay(1000)
            timeCount++
        }
    }

    var isDialogShowed by remember { mutableStateOf(false) }
    if (isDialogShowed) {
        val activity = LocalContext.current as Activity
        PermissionDialog(
            onConfirm = {
                showDndSettings(activity)
                isDialogShowed = false
            },
            onCancel = {
                isCounting = !isCounting
                isDialogShowed = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            val context = LocalContext.current
            StartFocusFAB(isCounting) {
                if (!isCounting && timeCount == 0) {
                    val m = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    if (!m.isNotificationPolicyAccessGranted)
                        isDialogShowed = true
                    else {
                        m.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
                        isCounting = !isCounting
                    }
                }
                else {
                    isCounting = !isCounting
                }
            }
        },
        isFloatingActionButtonDocked = true,
        bottomBar = { NavBar(navController, MaterialTheme.colorScheme.background) }
    ) { innerPadding ->
        val displayText = TimeUtils.toTimeNotation(timeCount)

        Column(
            modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                Modifier
                    .statusBarsPadding()
                    .fillMaxHeight(0.7f)
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceContainer,
                        RoundedCornerShape(0.dp, 0.dp, 100.dp, 100.dp)
                    ),
                Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Box(
                        Modifier
                            .height(128.dp)
                            .fillMaxWidth(),
                        Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Focus Page",
                                fontSize = 20.sp,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Stop watch",
                                color = Color(0xFFA5A5A5),
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }
                    }
                    Column(
                        Modifier.wrapContentHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = displayText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 72.sp,
                            color=MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Box(
                Modifier.fillMaxSize(),
                Alignment.BottomCenter
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "(ง๑ •̀_•́)ง", Modifier.padding(top = 12.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = "Non disturb mode will be started",
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(bottom = 48.dp)
                    )
                }
            }

        }
    }
}

private fun showDndSettings(activity: Activity) {
    val intent = Intent("android.settings.NOTIFICATION_POLICY_ACCESS_SETTINGS");
    activity.startActivity(intent);
}


@Composable
fun StartFocusFAB(isCounting: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(onClick, modifier, CircleShape) {
        when (isCounting) {
            true -> Icon(painterResource(R.drawable.ic_pause), "Create a new task")
            else -> Icon(Icons.Default.PlayArrow, "Create a new task")
        }
    }
}

@Composable
fun PermissionDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = { onCancel() },
        title = {
            Text(
                "Require permission",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                "to enable Do not disturb mode.",
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(
                    MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
            ) {
                Text("Grant")
            }
        },
        dismissButton = {
            Button(
                onClick = { onConfirm() },
                colors = ButtonDefaults.buttonColors(
                    Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                elevation = null
            ) {
                Text("Not now")
            }
        },
    )
}