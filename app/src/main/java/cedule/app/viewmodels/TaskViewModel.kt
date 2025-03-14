package cedule.app.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cedule.app.data.Database
import cedule.app.data.entities.Task
import cedule.app.utils.AlarmUtils
import cedule.app.utils.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskViewModel @Inject constructor(private val db: Database) : ViewModel() {
    private val _selectedDate = MutableStateFlow<Long?>(TimeUtils.getTodayMidnight())
    val selectedDate get() = _selectedDate

    fun setDate(date: Long?) {
        _selectedDate.value = date
    }

    private var orderType = MutableStateFlow(Sort.SORT_BY_DEFAULT)

    var todayTaskCount = db.tasksDAO().countTasks(TimeUtils.getTodayMidnight())

    @OptIn(ExperimentalCoroutinesApi::class)
    val tasks: StateFlow<List<Task>> = combine(_selectedDate, orderType) { date, order ->
        val taskFlow = when (date) {
            null -> db.tasksDAO().getAll()
            else -> db.tasksDAO().getByDate(date)
        }

        taskFlow.map { items ->
            when (order) {
                Sort.SORT_BY_DEFAULT -> items
                Sort.SORT_BY_NAME_ASC -> items.sortedBy { it.title }
                Sort.SORT_BY_NAME_DESC -> items.sortedByDescending { it.title }
                Sort.SORT_BY_DEADLINE -> items.sortedBy { it.startTime }
            }
        }
    }.flatMapLatest { sortedTasksFlow ->
        sortedTasksFlow
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var pendingDelete = MutableStateFlow<List<Int>>(emptyList())

    fun setDone(id: Int, isDone: Boolean) {
        viewModelScope.launch {
            db.tasksDAO().updateStatus(id, if (isDone) 1 else 0)
        }
    }

    fun setOrder(sort: Sort) {
        orderType.value = sort
    }

    fun getTask(id: Int): Flow<Task?> {
        if (id == -1)
            return emptyFlow()
        return db.tasksDAO().getById(id)
    }

    fun discard(context: Context, tasks: List<Int>) {
        viewModelScope.launch {
            tasks.forEach {
                AlarmUtils.cancelAlarm(context, it)
            }
            db.tasksDAO().discard(tasks)
            pendingDelete.value = emptyList()
        }
    }

    fun saveTask(context: Context, task: Task) {
        viewModelScope.launch {
            val id = db.tasksDAO().insertOrUpdate(task)

            if (task.isNotify && (task.startDate != null || task.startTime != null)) {
                val latestTask = Task(id.toInt(), startDate =task.startDate, startTime =task.startTime)
                AlarmUtils.setAlarm(context, latestTask)
            }
        }
    }
}

enum class Sort {
    SORT_BY_DEFAULT,
    SORT_BY_NAME_ASC,
    SORT_BY_NAME_DESC,
    SORT_BY_DEADLINE,
}