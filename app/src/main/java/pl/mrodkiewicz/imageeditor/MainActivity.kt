package pl.mrodkiewicz.imageeditor

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.onDispose
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.skydoves.landscapist.glide.GlideImage
import dagger.hilt.android.AndroidEntryPoint
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.FilterMatrix
import pl.mrodkiewicz.imageeditor.data.convolutionMatrix1
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
                R.drawable.cde,
                BitmapFactory.Options().apply {
                    this.inScaled = false
                })
        )
        setContent {
            ImageEditorTheme {
                MainScreen(mainViewModel)
            }
        }
        mainViewModel.bitmap.observe(this,  {
            setContent {
                ImageEditorTheme {
                    MainScreen(mainViewModel)
                }
            }
        })
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
//                AddFilterDialog(
//                    filters = mainViewModel.filters.value,
//                    onAddFilter = {},
//                    onDismiss = {},
//                )
            MainView(mainViewModel)
        }
    }

    @Composable
    fun MainView(mainViewModel: MainViewModel) {
        val clock = AnimationClockAmbient.current
        val pagerState = remember(clock) { PagerState(clock) }
        val filters = mainViewModel.filters.collectAsState()
        val bitmap = mainViewModel.bitmap.observeAsState()
        val displayMetrics = DisplayMetrics()


        Box {
            Surface(Modifier.fillMaxHeight().fillMaxWidth()) {
                ImagePreview(bitmap.value)
            }
            Text(text = filters.value[0].value.toString(), color = Color.White)
            FollowedPodcasts(
                mainViewModel = mainViewModel,
                items = filters.value,
                pagerState = pagerState,
                modifier = Modifier.padding(top = 16.dp).fillMaxWidth().fillMaxHeight())
        }
    }

    @Composable
    fun ImagePreview(bitmap: Bitmap?) {
        WithConstraints{
            mainViewModel.setWidth(width= constraints.maxWidth)
            bitmap?.let { Image(
                it.asImageAsset(),
                contentScale = ContentScale.FillWidth
            ) } ?: run {
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
                    mainViewModel.updateFilter(index, (value).toInt())
                },
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
        Column(modifier.padding(horizontal = 0.dp, vertical = 0.dp)) {
            Surface(
                Modifier.align(Alignment.CenterHorizontally).width(120.dp).wrapContentHeight()
                    .background(Color.Black)
            ) {
                Row(Modifier.align(Alignment.CenterHorizontally).padding(8.dp)) {
                    Image(
                        asset = vectorResource(filter.icon),
                        colorFilter = ColorFilter.tint(filter.customColor),
                        modifier = Modifier.width(48.dp).height(48.dp)
                            .absolutePadding(right = 12.dp)
                            .align(Alignment.CenterVertically)
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


}
