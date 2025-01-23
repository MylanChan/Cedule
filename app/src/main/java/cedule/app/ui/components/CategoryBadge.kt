package cedule.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cedule.app.viewmodels.CategoryViewModel

@Composable
fun CategoryBadge(categoryId: Int?, modifier: Modifier = Modifier) {
    categoryId?.let { id ->
        val categoryVM: CategoryViewModel = hiltViewModel()
        val category by categoryVM.getCategory(id).collectAsState(null)

        category?.let {
            val badgeColor =
                if (it.color != null) Color(it.color) else MaterialTheme.colorScheme.primaryContainer

            Box(
                modifier
                    .clip(RoundedCornerShape(15.dp))
                    .background(badgeColor)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    it.name,
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
