package pl.mrodkiewicz.imageeditor.ui.editorscreen

import android.graphics.Bitmap
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.mrodkiewicz.imageeditor.data.LutFilter


enum class TabsState { FILTERS, ADJUST }

@Composable
fun BottomActionBar(
    lutFilters: List<LutFilter>,
    activeFilter: Int = -1,
    onTabChange: (Int) -> Unit,
    onFilterSelected: (Int, LutFilter) -> Unit,
) {
    val (currentTab, setCurrentTab) = remember { mutableStateOf(0) }
    val transitionState = remember{ MutableTransitionState(TabsState.FILTERS) }
    val transition = updateTransition(transitionState)
    val height = transition.animateDp(transitionSpec = { tween(durationMillis = 300) }) {
        if (it == TabsState.FILTERS) 100.dp else 45.dp
    }


    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.Bottom) {
        Box(modifier = Modifier.height(height.value)) {
            when (currentTab) {
                0 -> {
                    LazyRow{
                        itemsIndexed(lutFilters) {
                            index, item ->
                                item.thumbnail?.let { it1 ->
                                    LutFilterItem(item.name, it1, index == activeFilter) {
                                        onFilterSelected.invoke(
                                            index,
                                            item
                                        )
                                    }
                                }
                        }
                    }

                }
                else -> Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Spacer(modifier = Modifier.weight(0.5f))
                    Text("slide left / hard to change a value, \n move up / down to select option", color = Color.White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.weight(0.5f))

                }
            }
        }

    }
    BottomTabs(tabs = TabsState.values(), currentTab) {
        setCurrentTab(it)
        onTabChange.invoke(it)
    }
}


@Composable
fun LutFilterItem(name: String, image: Bitmap, isActive: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isActive) Color.Black else Color(17, 17, 17)
    Column(
        modifier = Modifier
            .clickable(onClick = { onClick.invoke() })
            .background(backgroundColor)
            .padding(8.dp, 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = image.asImageBitmap(),
            "",
            Modifier
                .size(48.dp)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isActive) {
            Text(
                name,
                modifier = Modifier,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                color = Color.White
            )
        } else {
            Text(
                name,
                modifier = Modifier,
                textAlign = TextAlign.Center,
                color = Color.White
            )

        }
    }
}

@Composable
fun BottomTabs(tabs: Array<TabsState>, currentTab: Int = 0, onTabChange: (Int) -> Unit) {
    val weight = 1f / tabs.size
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, item ->
            Box(
                Modifier
                    .clickable(onClick = { onTabChange.invoke(index) })
                    .weight(weight)
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                if (currentTab == index) {
                    Text(
                        item.name,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                } else {
                    Text(
                        item.name,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                    )
                }
            }

        }
    }
}