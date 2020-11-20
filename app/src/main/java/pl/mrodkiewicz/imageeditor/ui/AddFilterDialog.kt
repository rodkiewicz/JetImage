package pl.mrodkiewicz.imageeditor.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyRowFor
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import pl.mrodkiewicz.imageeditor.data.Filter

@Composable
fun AddFilterDialog(
    filters : List<Filter>,
    onAddFilter: (Filter) -> Unit,
    onDismiss: () -> Unit){

    Dialog(onDismissRequest = onDismiss) {
        LazyColumnFor(items = filters.toList()) {
            Column(modifier = Modifier.clickable(onClick = { onAddFilter.invoke(it) })) {
                Text(text = "Item ${it.name}")
            }
        }
    }
}