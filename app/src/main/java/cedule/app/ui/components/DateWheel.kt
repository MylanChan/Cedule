package cedule.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
        lazyListState.animateScrollToItem(getDaysFromFirstOfYear())
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
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val scope = rememberCoroutineScope()
            TodayButton(selectedDate != TimeUtils.getTodayMidnight()) { today ->
                scope.launch { lazyListState.animateScrollToItem(today) }
            }
        }

        val firstVisibleIdx by remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }
        Box {
            Box(
                Modifier
                    .padding(top=8.dp, start=24.dp)
                    .height(24.dp),
                Alignment.Center
            ) {
                Text(
                    dates[firstVisibleIdx].month,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            LazyRow(
                Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                lazyListState
            ) {
                itemsIndexed(dates) { idx, date ->
                    val isMonthShowed = date.day == 1 && idx != firstVisibleIdx
                    DateEntry(date, isMonthShowed, Modifier.fillParentMaxWidth(1f / 7f))
                }
            }
        }
    }
}

@Composable
private fun DateEntry(date: DateInfo, isShowMonth: Boolean, modifier: Modifier = Modifier) {
    val taskVM = hiltViewModel<TaskViewModel>()
    val selectedDate by taskVM.selectedDate.collectAsState(TimeUtils.getTodayMidnight())
    val isSelected by remember { derivedStateOf { selectedDate == date.timestamp } }

    Box(
        modifier
            .scale(if (isSelected) 1.15f else 1.0f)
            .padding(4.dp),
        Alignment.Center
    ) {
        Column(
            Modifier,
            Arrangement.Bottom,
            Alignment.CenterHorizontally,
        ) {
            Box(
                Modifier
                    .padding(top=4.dp)
                    .height(24.dp),
                Alignment.Center,
            ) {
                if (isShowMonth)
                    Text(
                        date.month,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
            }

            Surface(shadowElevation = 4.dp, shape = RoundedCornerShape(15.dp)) {
                Column(
                    Modifier
                        .background(
                            if (isSelected)
                                MaterialTheme.colorScheme.inverseSurface
                            else
                                MaterialTheme.colorScheme.surfaceContainer
                        )
                        .fillMaxWidth()
                        .clickable {
                            taskVM.setDate(if (isSelected) null else date.timestamp)
                        }
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        date.day.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color =
                            if (isSelected)
                                MaterialTheme.colorScheme.inverseOnSurface
                            else
                                MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        date.weekday,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

fun getDaysFromFirstOfYear(): Int {
    val calendar = Calendar.getInstance()

    val firstDayOfYear = Calendar.getInstance().apply {
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val diffInMillis = calendar.timeInMillis - firstDayOfYear.timeInMillis
    return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
}



@Composable
private fun TodayButton(isShowed: Boolean, modifier: Modifier = Modifier, onClick: (Int) -> Unit) {
    val taskVM = hiltViewModel<TaskViewModel>()
    val todayMidnight = TimeUtils.getTodayMidnight()

    AnimatedVisibility(isShowed, enter = fadeIn(), exit = slideOutHorizontally { it / 4 }) {
        Box(
            modifier
                .padding(horizontal = 4.dp)
                .height(32.dp)
                .background(
                    MaterialTheme.colorScheme.tertiaryContainer,
                    RoundedCornerShape(15.dp)
                )
                .clickable {
                    taskVM.setDate(todayMidnight);
                    onClick(getDaysFromFirstOfYear())
                },
            Alignment.Center
        ) {
            Row(Modifier.padding(horizontal = 8.dp), Arrangement.Center, Alignment.CenterVertically) {
                Text(
                    "Today",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Icon(
                    rememberVectorPainter(Icons.Filled.KeyboardArrowRight),
                    "Return today's task list",
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}