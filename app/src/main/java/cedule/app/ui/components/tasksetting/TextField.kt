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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.data.entities.Category
import cedule.app.ui.components.IconBox
import cedule.app.viewmodels.CategoryViewModel

@Composable
private fun MenuItem(text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text, style = MaterialTheme.typography.labelMedium) },
        onClick = { onClick() }
    )
}

@Composable
private fun ColorBox(
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (Color) -> Unit
) {
    IconButton(onClick = { onClick(color) }, modifier) {
        Box(
            Modifier
                .size(24.dp)
                .background(color, CircleShape)
        )
    }
}

@Composable
private fun CategoryColorBox(c: Category?) {
    val color = when (c == null) {
        true -> MaterialTheme.colorScheme.primary
        else -> Color(c.color!!)
    }

    ColorBox(color) {

    }
}

@Composable
fun AutoCompleteTextField(modifier: Modifier = Modifier) {
    var textInput by remember { mutableStateOf(TextFieldValue("")) }
    var isMenuExpanded by remember { mutableStateOf(false) }

    val categoryVM: CategoryViewModel = hiltViewModel()
    val currentCategory by categoryVM.getByName(textInput.text).collectAsState(null)

    Box(modifier) {
        OutlinedTextField(
            textInput,
            onValueChange = {
                if (it.text.length > 15) return@OutlinedTextField
                textInput = it

                isMenuExpanded = it.text.isNotEmpty()
            },
            trailingIcon = {
                when (textInput.text.isNotEmpty()) {
                    true -> CategoryColorBox(currentCategory)
                    else -> IconBox(Icons.Default.ArrowDropDown, "Expand drop down menu") {
                        isMenuExpanded = !isMenuExpanded
                    }
                }
            },
            placeholder = { Text("Category", style = MaterialTheme.typography.labelMedium) },
            singleLine = true
        )

        // show category suggestion based on the user input
        val suggestions by categoryVM.getSimilar(textInput.text)
            .collectAsState(emptyList())

        SuggestionDropdownMenu(isMenuExpanded, suggestions, { isMenuExpanded = it }) {
            textInput = TextFieldValue(it.name, TextRange(it.name.length))
        }
    }
}

@Composable
private fun SuggestionDropdownMenu(
    isExpanded: Boolean,
    categories: List<Category>,
    setIsExpanded: (Boolean) -> Unit,
    onClickItem: (Category) -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = { setIsExpanded(false) },
        properties = PopupProperties(focusable = false)
    ) {
        if (categories.isNotEmpty()) {
            categories.forEach {
                MenuItem(it.name) {
                    onClickItem(it)
                    setIsExpanded(false)
                }
            }
        }
    }
}