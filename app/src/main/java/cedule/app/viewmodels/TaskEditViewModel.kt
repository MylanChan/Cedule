package cedule.app.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cedule.app.data.entities.Task
import kotlinx.coroutines.launch

class TaskEditViewModel : ViewModel() {
    var id by mutableStateOf<Int?>(null)
    var createTime by mutableStateOf<Int?>(null)
    var title by mutableStateOf("")
    var category by mutableStateOf<Int?>(null)
    var startDate by mutableStateOf<Long?>(null)
    var startTime by mutableStateOf<Int?>(null)
    var isDone by mutableStateOf(false)
    var isNotify by mutableStateOf(false)
    var note by mutableStateOf("")

    var categoryName by mutableStateOf("")
    var categoryColor by mutableLongStateOf(0xFF7BCCF6)

    fun saveTask(onSave: (Task) -> Unit) {
        val t = Task(id, createTime, title, category, startDate, startTime, isDone, isNotify, note)
        viewModelScope.launch { onSave(t) }
    }

    fun loadTask(task: Task) {
        id = task.id
        createTime = task.createTime
        title = task.title ?: ""
        category = task.category
        startDate = task.startDate
        startTime = task.startTime
        isDone = task.isDone
        isNotify = task.isNotify
        note = task.note ?: ""
    }
}
