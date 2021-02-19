package pl.mrodkiewicz.imageeditor.ui.editorscreen

import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import pl.mrodkiewicz.imageeditor.MainViewModel
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.data.AdjustFilter
import pl.mrodkiewicz.imageeditor.ui.actionBarTextStyle

@ExperimentalComposeApi
@Composable
fun EditorScreen(writePermissionState: PermissionState, mainViewModel: MainViewModel) {
    val context = AmbientContext.current
    Column {
        Surface(
            Modifier.fillMaxWidth().height(56.dp).background(MaterialTheme.colors.primary)
        ) {
            Row(
                Modifier.background(MaterialTheme.colors.secondary).align(Alignment.CenterHorizontally).padding(start = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "JetPhoto",
                    style = actionBarTextStyle,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    NeedsPermission(writePermissionState, {
                        IconButton(onClick = {
                            mainViewModel.save { uri ->
                                startActivity(
                                    context,
                                    Intent(Intent.ACTION_VIEW, uri).apply {
                                        addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                                    },null
                                )
                            }
                        }) {
                            Image(
                                imageVector = vectorResource(R.drawable.ic_baseline_done_24),
                                colorFilter = ColorFilter.tint(Color(235, 183, 22)),
                                modifier = Modifier.width(24.dp).height(24.dp)
                            )
                        }
                    }, { IconButton(onClick = {
                        writePermissionState.launchPermissionRequest()
                    }) {
                        Image(
                            imageVector = vectorResource(R.drawable.ic_baseline_done_24),
                            colorFilter = ColorFilter.tint(Color(150, 150, 150)),
                            modifier = Modifier.width(24.dp).height(24.dp)
                        )
                    } })

                }

            }
        }
        EditorView(mainViewModel)
    }
}

@Composable
fun EditorView(mainViewModel: MainViewModel) {
    val clock = AmbientAnimationClock.current
    val pagerState = remember(clock) { EditorPagerState(clock) }
    val filters = mainViewModel.filters.collectAsState()
    val bitmap = mainViewModel.bitmap.collectAsState()
    val lutFilters = mainViewModel.lut.collectAsState()
    val activeFilter = mainViewModel.activeLutIndex.collectAsState()
    val (visible, setVisibility) = remember { mutableStateOf(false) }

    Column {
        Box(Modifier.weight(1f)) {
            Surface(Modifier.fillMaxHeight().fillMaxWidth()) {
                ImagePreview(mainViewModel, bitmap.value)
            }
            FilterControl(
                mainViewModel = mainViewModel,
                items = filters.value,
                visible = visible,
                pagerState = pagerState,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth().fillMaxHeight()
            )

        }
        BottomActionBar(
            lutFilters = lutFilters.value,
            activeFilter = activeFilter.value,
            onTabChange = {
                if (it == 1) {
                    setVisibility(true)
                } else {
                    setVisibility(false)
                }
            }, { index, item -> mainViewModel.setLUTFilter(index, item) }
        )
    }

}

@Composable
fun ImagePreview(mainViewModel: MainViewModel, bitmap: Bitmap?) {
    WithConstraints {
        mainViewModel.setWidth(width = constraints.maxWidth)
        bitmap?.let {
            Image(
                it.asImageBitmap(),
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
            )
        } ?: run {
            Text(
                text = "LOADING....",
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
    items: List<AdjustFilter>,
    visible: Boolean = false,
    pagerState: EditorPagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) { EditorPagerState(clock) }
    }
) {
    if (items.isNotEmpty()) {
        pagerState.maxPage = (items.size - 1).coerceAtLeast(0)
        EditorPager(
            state = pagerState,
            modifier = modifier,
            visible = visible,
            onValueChange = { index, value ->
                mainViewModel.updateAdjustFilter(index, (value).toInt())
            },
        ) {
            EditorPagerItem(
                adjustFilter = items[page],
                modifier = Modifier.padding(4.dp).fillMaxHeight().scalePagerItems()
            )
        }
    }
}

@Composable
private fun EditorPagerItem(adjustFilter: AdjustFilter, modifier: Modifier = Modifier) {
    Column(modifier.padding(horizontal = 0.dp, vertical = 0.dp)) {
        Surface(
            Modifier.align(Alignment.CenterHorizontally).width(120.dp).wrapContentHeight()
                .background(Color.Black)
        ) {
            Row(Modifier.align(Alignment.CenterHorizontally).padding(8.dp)) {
                Image(
                    imageVector = vectorResource(adjustFilter.icon),
                    colorFilter = ColorFilter.tint(adjustFilter.customColor),
                    modifier = Modifier.width(48.dp).height(48.dp)
                        .absolutePadding(right = 12.dp)
                )
                Column {
                    Text(
                        text = adjustFilter.name,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        (adjustFilter.value).toString(),
                        Modifier.padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally),
                        Color.Unspecified,
                        TextUnit.Unspecified,
                        null,
                        null,
                        null,
                        TextUnit.Unspecified,
                        null,
                        null,
                        TextUnit.Unspecified,
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
