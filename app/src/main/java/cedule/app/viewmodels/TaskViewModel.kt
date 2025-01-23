package cedule.app.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cedule.app.data.Database
import cedule.app.data.entities.Task
import cedule.app.services.TaskNotifyReceiver
import cedule.app.utils.AlarmUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaskViewModel @Inject constructor(private val db: Database) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    var pendingDelete = MutableStateFlow<List<Int>>(emptyList())

    private fun updateTasks(flow: Flow<List<Task>>) {
        viewModelScope.launch {
            flow.collect { _tasks.value = it }
        }
    }

    init {
        updateTasks(db.tasksDAO().getAll())
    }

    fun setDone(id: Int, isDone: Boolean) {
        viewModelScope.launch {
            db.tasksDAO().updateStatus(id, if (isDone) 1 else 0)
        }
    }

    fun setOrder(type: Sort) {
        val flow = when (type) {
            Sort.SORT_BY_NAME_DESC -> db.tasksDAO().getInNameAsc()
            Sort.SORT_BY_NAME_ASC -> db.tasksDAO().getInNameDesc()
            Sort.SORT_BY_DEADLINE -> db.tasksDAO().getInDeadline()
        }
        updateTasks(flow)
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
            db.tasksDAO().insertOrUpdate(task)

            if (task.isNotify == 1 && (task.startDate != null || task.startTime != null)) {
                val latestTask = when (task.id == null) {
                    true -> db.tasksDAO().getLatestTask()!!
                    else -> task
                }
                AlarmUtils.setAlarm(context, latestTask)
            }
        }
    }
}

enum class Sort {
    SORT_BY_NAME_ASC,
    SORT_BY_NAME_DESC,
    SORT_BY_DEADLINE,
}