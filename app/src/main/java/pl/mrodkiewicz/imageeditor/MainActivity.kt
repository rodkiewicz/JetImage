package pl.mrodkiewicz.imageeditor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.default_filters
import pl.mrodkiewicz.imageeditor.ui.ImageEditorTheme
import pl.mrodkiewicz.imageeditor.ui.Pager
import pl.mrodkiewicz.imageeditor.ui.PagerState
import pl.mrodkiewicz.imageeditor.ui.actionBarTextStyle


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.setBitmap(
            BitmapFactory.decodeResource(
                this.resources,
                R.drawable.abc
            )
        )
        mainViewModel.filters.observe(this) {
            setContent {
                ImageEditorTheme {
                    MainScreen(mainViewModel)
                }
            }
        }
    }

    @Composable
    fun MainScreen(mainViewModel: MainViewModel) {
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
            MainView(mainViewModel)
        }
    }

    @Composable
    fun MainView(mainViewModel: MainViewModel) {
        val clock = AnimationClockAmbient.current
        val pagerState = remember(clock) { PagerState(clock) }
        val filters: List<Filter> by mainViewModel.filters.observeAsState(default_filters)
        Stack {
            Surface(Modifier.fillMaxHeight().fillMaxWidth()) {
                mainViewModel.bitmap.value?.let { ImagePreview(it) }
            }
            Text(text = filters[0].value.toString(), color = Color.White)
            FollowedPodcasts(
                mainViewModel = mainViewModel,
                items = filters,
                pagerState = pagerState,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth().fillMaxHeight()
            )

        }
    }

    @Composable
    fun ImagePreview(bitmap: Bitmap) {
        Image(
            modifier = Modifier.fillMaxHeight().fillMaxWidth()
                .background(Color.Black), asset = bitmap.asImageAsset()
        )
    }

    @Composable
    fun FollowedPodcasts(
        mainViewModel: MainViewModel,
        items: List<Filter>,
        pagerState: PagerState = run {
            val clock = AnimationClockAmbient.current
            remember(clock) { PagerState(clock) }
        }, modifier: Modifier = Modifier
    ) {
        if (items.isNotEmpty()) {
            pagerState.maxPage = (items.size - 1).coerceAtLeast(0)
            Pager(
                state = pagerState,
                modifier = modifier,
                onValueChange = { index, value ->
                    mainViewModel.updateFilter(index, value)
                }
            ) {
                FollowedPodcastCarouselItem(
                    filter = items[page],
                    modifier = Modifier.padding(4.dp).fillMaxHeight().scalePagerItems()
                )
            }
        }
    }

    @Composable
    private fun FollowedPodcastCarouselItem(filter: Filter, modifier: Modifier = Modifier) {
        Column(modifier.padding(horizontal = 0.dp, vertical = 8.dp)) {
            Surface(
                Modifier.align(Alignment.CenterHorizontally).width(100.dp).wrapContentHeight()
                    .background(Color.Black)
            ) {
                Column(Modifier.align(Alignment.CenterHorizontally).padding(8.dp)) {
                    Image(
                        asset = vectorResource(filter.icon),
                        colorFilter = ColorFilter.tint(filter.customColor),
                        modifier = Modifier.width(80.dp).height(80.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = filter.name,
                        style = MaterialTheme.typography.caption,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        text = (filter.value / 100).toString(),
                        style = MaterialTheme.typography.subtitle1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                }
            }

        }
    }
}

