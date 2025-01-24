package cedule.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.utils.DateInfo
import cedule.app.utils.TimeUtils
import cedule.app.viewmodels.TaskViewModel
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun DateWheel(modifier: Modifier = Modifier) {
    val currentDate = Calendar.getInstance()
    val dates = TimeUtils.getAllDatesForYear(currentDate.get(Calendar.YEAR))

    val taskVM = hiltViewModel<TaskViewModel>()
    val selectedDate by taskVM.selectedDate.collectAsState(TimeUtils.getTodayMidnight())

    val lazyListState = rememberLazyListState()
    LaunchedEffect(Unit) {
        lazyListState.scrollToItem(getDaysFromFirstOfYear())
    }

    Column {
        Row(
            modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                currentDate.weekYear.toString(),
                Modifier.weight(1f).fillMaxHeight(),
                style = MaterialTheme.typography.titleMedium
            )

            val scope = rememberCoroutineScope()
            TodayButton(selectedDate != TimeUtils.getTodayMidnight()) { today -> scope.launch { lazyListState.scrollToItem(today) } }
        }

        LazyRow(Modifier.fillMaxWidth(), lazyListState) {
            items(dates) {
                DateEntry(it, Modifier.fillParentMaxWidth(1f / 7f))
            }
        }
    }
}

@Composable
private fun DateEntry(date: DateInfo, modifier: Modifier = Modifier) {
    val taskVM = hiltViewModel<TaskViewModel>()
    val selectedDate by taskVM.selectedDate.collectAsState(TimeUtils.getTodayMidnight())
println(selectedDate.toString() + " " + date.timestamp)

    Box(
        modifier
            .padding(4.dp)
            .clickable {
                println(taskVM.selectedDate.value)
                taskVM.setDate(if (selectedDate == date.timestamp) null else date.timestamp)
            },
        Alignment.Center
    ) {
        Column(
            Modifier
                .background(
                    if (selectedDate == date.timestamp)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceContainer,
                    RoundedCornerShape(15.dp)
                ),
            Arrangement.Bottom,
            Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .padding(top=4.dp)
                    .height(24.dp),
                Alignment.Center
            ) {
                if (date.day == 1)
                    Text(date.month, style = MaterialTheme.typography.bodySmall)
            }

            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(date.day.toString(), style = MaterialTheme.typography.bodyLarge)
                Text(date.weekday, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun getDaysFromFirstOfYear(): Int {
    val calendar = Calendar.getInstance()

    // Set the calendar to January 1st of the current year
    val firstDayOfYear = Calendar.getInstance().apply {
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    // Calculate the difference in days
    val diffInMillis = calendar.timeInMillis - firstDayOfYear.timeInMillis
    return (diffInMillis / (1000 * 60 * 60 * 24)).toInt() // Convert milliseconds to days
}



@Composable
private fun TodayButton(isShowed: Boolean, modifier: Modifier = Modifier, onClick: (Int) -> Unit) {
    val taskVM = hiltViewModel<TaskViewModel>()
    val todayMidnight = TimeUtils.getTodayMidnight()

    AnimatedVisibility(isShowed) {
        Box(
            modifier
                .padding(horizontal = 4.dp)
                .height(32.dp)
                .width(52.dp)
                .background(
                    MaterialTheme.colorScheme.tertiaryContainer,
                    RoundedCornerShape(15.dp)
                )
                .clickable {
                    taskVM.setDate(todayMidnight);
                    onClick(getDaysFromFirstOfYear())
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Today",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}