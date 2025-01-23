package cedule.app.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp

@Composable
fun IconBox(
    @DrawableRes iconRes: Int,
    contentDesc: String,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {}
) {
    IconBox(onClick, modifier) {
        Icon(ImageVector.vectorResource(iconRes), contentDesc, tint = tint)
    }
}

@Composable
fun IconBox(
    vector: ImageVector,
    contentDesc: String,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit = {}
) {
    IconBox(onClick, modifier) {
        Icon(rememberVectorPainter(vector), contentDesc, tint = tint)
    }
}

@Composable
fun IconBox(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    Box(
        modifier
            .size(48.dp)
            .clickable(onClick=onClick),
        Alignment.Center,
        content = { icon() }
    )
}