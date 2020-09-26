package pl.mrodkiewicz.imageeditor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ThemeUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageAsset
import androidx.compose.ui.platform.AnimationClockAmbient
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
import pl.mrodkiewicz.imageeditor.data.FilterUI
import pl.mrodkiewicz.imageeditor.ui.*
import timber.log.Timber
import java.time.format.TextStyle


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("start")


        setContent {
            ImageEditorTheme {
                MainScreen()
            }
        }
    }
}

@Preview
@Composable
fun MainScreen() {
    Column(Modifier.background(Color.Yellow)) {
        Surface(Modifier.fillMaxWidth().height(56.dp).background(MaterialTheme.colors.primary)) {
            Row(Modifier.background(MaterialTheme.colors.secondary).gravity(Alignment.CenterHorizontally).padding(start = 8.dp)) {
                Text(
                    text = "JetEditor",
                    style = actionBarTextStyle,
                    modifier = Modifier.gravity(Alignment.CenterVertically)
                )
            }
        }
        MainView()
    }
}

@Composable
fun MainView() {
    val clock = AnimationClockAmbient.current
    val pagerState = remember(clock) { PagerState(clock) }
    val context = ContextAmbient.current
    val bitmap = remember {
        BitmapFactory.decodeResource(
            context.resources,
            R.drawable.abc
        )
    }
    Stack {
        Surface(
            Modifier.fillMaxHeight().fillMaxWidth()
        ) {
            ImagePreview(bitmap)
        }
        FollowedPodcasts(
            items = listOf(
                FilterUI(name = "Filtr 1"),
                FilterUI(name = "Filtr 2"),
                FilterUI(name = "Filtr 3"),
                FilterUI(name = "Filtr 4"),
                FilterUI(name = "Filtr 5"),
            ),
            pagerState = pagerState,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight()
        )


    }
}

@Composable
fun ImagePreview(bitmap: Bitmap) {
    Image(
        modifier = Modifier.fillMaxHeight().fillMaxWidth().gravity(Alignment.CenterHorizontally)
            .background(Color.Black),
        asset = bitmap.asImageAsset()
    )
}

@Composable
fun FollowedPodcasts(
    items: List<FilterUI>,
    pagerState: PagerState = run {
        val clock = AnimationClockAmbient.current
        remember(clock) { PagerState(clock) }
    },
    modifier: Modifier = Modifier
) {
    pagerState.maxPage = (items.size - 1).coerceAtLeast(0)
    Pager(
        state = pagerState,
        modifier = modifier,
    ) {
        FollowedPodcastCarouselItem(
            filterUI = items[page],
            modifier = Modifier.padding(4.dp)
                .fillMaxHeight().scalePagerItems()
        )
    }
}

@Composable
private fun FollowedPodcastCarouselItem(
    filterUI: FilterUI,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.padding(horizontal = 0.dp, vertical = 8.dp)
    ) {
        Stack(
            Modifier
                .gravity(Alignment.CenterHorizontally)
                .preferredWidth(100.dp).preferredHeight(100.dp).background(Color.DarkGray)
        ) {
        }
        ProvideEmphasis(EmphasisAmbient.current.medium) {
            Text(
                text = filterUI.name,
                color = Color.Blue,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
                    .gravity(Alignment.CenterHorizontally)
            )
        }
    }
}
