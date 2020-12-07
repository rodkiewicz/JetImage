package pl.mrodkiewicz.imageeditor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.AmbientTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.Dialog
import pl.mrodkiewicz.imageeditor.data.Filter

@Composable
fun AddFilterDialog(
    filters: List<Filter>,
    onAddFilter: (Filter) -> Unit,
    onDismiss: () -> Unit
) {

    Dialog(onDismissRequest = onDismiss) {
        LazyColumnFor(items = filters.toList()) {
            Column(modifier = Modifier.clickable(onClick = { onAddFilter.invoke(it) })) {
                Text(
                    "Item ${it.name}",
                    Modifier,
                    Color.Unspecified,
                    TextUnit.Inherit,
                    null,
                    null,
                    null,
                    TextUnit.Inherit,
                    null,
                    null,
                    TextUnit.Inherit,
                    TextOverflow.Clip,
                    true,
                    Int.MAX_VALUE,
                    {},
                    AmbientTextStyle.current
                )
            }
        }
    }
}