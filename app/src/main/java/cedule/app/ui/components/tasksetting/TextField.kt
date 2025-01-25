package cedule.app.ui.components.tasksetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.activities.ColorSelectionDialog
import cedule.app.data.entities.Category
import cedule.app.ui.components.IconBox
import cedule.app.viewmodels.CategoryViewModel
import cedule.app.viewmodels.TaskEditViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
private fun MenuItem(text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text, style = MaterialTheme.typography.labelMedium) },
        onClick = { onClick() }
    )
}

@Composable
private fun ColorBox(
    color: Long,
    modifier: Modifier = Modifier,
    onClick: (Color) -> Unit
) {
    IconButton(onClick = { onClick(Color(color)) }, modifier) {
        Box(
            Modifier
                .size(24.dp)
                .background(Color(color), CircleShape)
        )
    }
}

@Composable
private fun CategoryColorBox(modifier: Modifier = Modifier) {
    val editVM = hiltViewModel<TaskEditViewModel>()
    val color by remember { derivedStateOf { editVM.categoryColor } }

    var isDialogShowed by remember { mutableStateOf(false) }
    if (isDialogShowed) {
        ColorSelectionDialog(onDismiss = { isDialogShowed = false }) {
            editVM.categoryColor = it
        }
    }
    ColorBox(color, modifier) { isDialogShowed = true }
}

@Composable
fun AutoCompleteTextField(modifier: Modifier = Modifier) {
    val editVM: TaskEditViewModel = hiltViewModel()
    val categoryVM: CategoryViewModel = hiltViewModel()

    val textInput by remember { derivedStateOf { editVM.categoryName } }
    var isMenuExpanded by remember { mutableStateOf(false) }

    val currentCategory by categoryVM.getByName(textInput).collectAsState(null)

    val coroutine = CoroutineScope(Dispatchers.IO)

    val categoryId by remember { derivedStateOf { editVM.category } }
    LaunchedEffect(categoryId) {
        if (editVM.category != null) {
            coroutine.launch {
                categoryVM.getCategory(editVM.category!!).collect {
                    it?.let {
                        editVM.categoryName = it.name
                        editVM.categoryColor = it.color!!.toLong()
                    }
                }
            }
        }
    }

    LaunchedEffect(currentCategory) {
        currentCategory?.let {
            editVM.categoryName = it.name
            editVM.categoryColor = it.color!!.toLong()
        }
    }

    Box(modifier) {
        OutlinedTextField(
            textInput,
            onValueChange = {
                if (it.length > 15) return@OutlinedTextField
                editVM.categoryName = it

                isMenuExpanded = it.isNotEmpty()
            },
            trailingIcon = {
                when (textInput.isNotEmpty()) {
                    true -> CategoryColorBox()
                    else -> IconBox(Icons.Default.ArrowDropDown, "Expand drop down menu") {
                        isMenuExpanded = !isMenuExpanded
                    }
                }
            },
            placeholder = { Text("Category", style = MaterialTheme.typography.labelMedium) },
            singleLine = true
        )

        // show category suggestion based on the user input
        val suggestions by categoryVM.getSimilar(textInput)
            .collectAsState(emptyList())

        SuggestionDropdownMenu(isMenuExpanded, suggestions, { isMenuExpanded = it }) { name, color ->
            editVM.categoryName = name
            editVM.categoryColor = color
        }
    }
}

@Composable
private fun SuggestionDropdownMenu(
    isExpanded: Boolean,
    categories: List<Category>,
    setIsExpanded: (Boolean) -> Unit,
    onClickItem: (String, Long) -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { setIsExpanded(false) },
        properties = PopupProperties(focusable = false)
    ) {
        if (categories.isNotEmpty()) {
            categories.forEach {
                MenuItem(it.name) {
                    onClickItem(it.name, it.color!!.toLong())
                    setIsExpanded(false)
                }
            }
        }
    }
}