package pl.mrodkiewicz.imageeditor.ui

import android.graphics.Bitmap
import android.util.DisplayMetrics
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import pl.mrodkiewicz.imageeditor.MainViewModel
import pl.mrodkiewicz.imageeditor.data.Filter

@Composable
fun EditorScreen(mainViewModel: MainViewModel) {
    Column(Modifier.background(Color.Yellow)) {
        Surface(
            Modifier.fillMaxWidth().height(56.dp).background(MaterialTheme.colors.primary)
        ) {
            Row(
                Modifier.background(MaterialTheme.colors.secondary)
                    .align(Alignment.CenterHorizontally).padding(start = 8.dp)
            ) {
                Text(
                    text = "JetEditor",
                    style = actionBarTextStyle,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
//                AddFilterDialog(
//                    filters = mainViewModel.filters.value,
//                    onAddFilter = {},
//                    onDismiss = {},
//                )
        EditorView(mainViewModel)
    }
}

@Composable
fun EditorView(mainViewModel: MainViewModel) {
    val clock = AnimationClockAmbient.current
    val pagerState = remember(clock) { EditorPagerState(clock) }
    val filters = mainViewModel.filters.collectAsState()
    val bitmap = mainViewModel.bitmap.observeAsState()


    Box {
        Surface(Modifier.fillMaxHeight().fillMaxWidth()) {
            ImagePreview(mainViewModel,bitmap.value)
        }
        Text(text = filters.value[0].value.toString(), color = Color.White)
        FilterControl(
            mainViewModel = mainViewModel,
            items = filters.value,
            pagerState = pagerState,
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth().fillMaxHeight()
        )
    }
}

@Composable
fun ImagePreview(mainViewModel: MainViewModel,bitmap: Bitmap?) {
    WithConstraints {
        mainViewModel.setWidth(width = constraints.maxWidth)
        bitmap?.let {
            Image(
                it.asImageAsset(),
                contentScale = ContentScale.None
            )
        } ?: run {
            Text(
                text = "BITMAP IS NULL",
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )

        }

    }

}

@Composable
fun FilterControl(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    items: List<Filter>,
    pagerState: EditorPagerState = run {
        val clock = AnimationClockAmbient.current
        remember(clock) { EditorPagerState(clock) }
    }
) {
    if (items.isNotEmpty()) {
        pagerState.maxPage = (items.size - 1).coerceAtLeast(0)
        EditorPager(
            state = pagerState,
            modifier = modifier,
            onValueChange = { index, value ->
                mainViewModel.updateFilter(index, (value).toInt())
            },
        ) {
            EditorPagerItem(
                filter = items[page],
                modifier = Modifier.padding(4.dp).fillMaxHeight().scalePagerItems()
            )
        }
    }
}

@Composable
private fun EditorPagerItem(filter: Filter, modifier: Modifier = Modifier) {
    Column(modifier.padding(horizontal = 0.dp, vertical = 0.dp)) {
        Surface(
            Modifier.align(Alignment.CenterHorizontally).width(120.dp).wrapContentHeight()
                .background(Color.Black)
        ) {
            Row(Modifier.align(Alignment.CenterHorizontally).padding(8.dp)) {
                Image(
                    imageVector = vectorResource(filter.icon),
                    colorFilter = ColorFilter.tint(filter.customColor),
                    modifier = Modifier.width(48.dp).height(48.dp)
                        .absolutePadding(right = 12.dp)
                )
                Column {
                    Text(
                        text = filter.name,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        (filter.value).toString(),
                        Modifier.padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        Color.Unspecified,
                        TextUnit.Inherit,
                        null,
                        null,
                        null,
                        TextUnit.Inherit,
                        null,
                        null,
                        TextUnit.Inherit,
                        TextOverflow.Ellipsis,
                        true,
                        1,
                        {},
                        MaterialTheme.typography.subtitle1
                    )
                }
            }
        }

    }
}
