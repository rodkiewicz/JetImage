package pl.mrodkiewicz.imageeditor.ui

import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.transition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientConfiguration
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import pl.mrodkiewicz.imageeditor.R

private enum class AnimationState { COLLAPSED, EXTENDED }

private val actionBarHeight = DpPropKey("ActionBarHeight =")
private val actionBarTextSize = FloatPropKey("TextSize")
private val madeByTextPadding = DpPropKey("TextSize")
private val bottomCardPadding = DpPropKey("Padding")

@Composable
fun SplashScreen(navController: NavController) {
    val clock = AmbientAnimationClock.current
    var pagerState = remember(clock) { PagerState(clock) }


    var actionBarExtendedHeight =
        with(AmbientDensity.current) { (AmbientConfiguration.current.screenHeightDp / 2).dp }

    val animation = remember {
        transitionDefinition<AnimationState> {
            state(AnimationState.COLLAPSED) {
                this[actionBarHeight] = actionBarExtendedHeight
                this[actionBarTextSize] = 50f
                this[madeByTextPadding] = 8.dp
                this[bottomCardPadding] = 16.dp
            }
            state(AnimationState.EXTENDED) {
                this[actionBarHeight] = 56.dp
                this[actionBarTextSize] = 32f
                this[madeByTextPadding] = 1000.dp
                this[bottomCardPadding] = 1000.dp
            }
        }
    }
    val (selected, onSelected) = remember { mutableStateOf(false) }
    val animationState = transition(
        definition = animation,
        toState = if (selected) AnimationState.EXTENDED else AnimationState.COLLAPSED,
        onStateChangeFinished = {
            navController.navigate("editorScreen")
        }
    )

    Column {
        Surface(
            Modifier.fillMaxWidth().height(animationState[actionBarHeight])
                .toggleable(value = selected, onValueChange = onSelected)
                .background(MaterialTheme.colors.secondary)
        ) {
            Box(
                Modifier.background(MaterialTheme.colors.secondary).fillMaxSize()
                    .padding(start = 8.dp),
            ) {
                Text(
                    text = "JetEditor",
                    fontSize = animationState[actionBarTextSize].sp,
                    style = actionBarTextStyle,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "Made by IT SUPER STAR",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    style = actionBarTextStyle,
                    modifier = Modifier.align(Alignment.BottomEnd)
                        .padding(8.dp, animationState[madeByTextPadding], 8.dp, 8.dp)
                )

            }
        }
        Surface(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
            Card(
                shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp),
                modifier = Modifier
                    .padding(start = 16.dp, top = animationState[bottomCardPadding], end = 16.dp)
            ) {
                FollowedPodcasts()
            }
        }
    }
}

@Composable
fun helloPage(nextPageButtonClick: () -> Unit) {
    Column(
        modifier = Modifier.background(Color(15, 15, 15)).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Siema wybierz plik", color = Color.LightGray, fontSize = 28.sp)
        Spacer(Modifier.preferredSize(12.dp))
        Text(
            text = "xdwqda",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
        Spacer(Modifier.preferredSize(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Button(onClick = {}, modifier = Modifier.width(100.dp)) {
                Icon(
                    vectorResource(id = R.drawable.ic_add_a_photo_24),
                    modifier = Modifier.preferredSize(36.dp)
                )
            }
            Button(onClick = { nextPageButtonClick.invoke() }, modifier = Modifier.width(100.dp)) {
                Icon(
                    vectorResource(id = R.drawable.ic_add_photo_alternate_24),
                    modifier = Modifier.preferredSize(36.dp)
                )
            }
        }

    }
}

@Composable
fun lastPage() {
     Column(
        modifier = Modifier.background(Color(15, 15, 15)).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Siema wybierz plik", color = Color.LightGray, fontSize = 28.sp)
        Spacer(Modifier.preferredSize(12.dp))
        Text(
            text = "siema mordeczko wybierz plik z galerii czy tam aparatu jak wolisz sztywniuko mordo",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
        Spacer(Modifier.preferredSize(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Button(onClick = {}, modifier = Modifier.width(100.dp)) {
                Icon(
                    vectorResource(id = R.drawable.ic_add_a_photo_24),
                    modifier = Modifier.preferredSize(36.dp)
                )
            }
            Button(onClick = {}, modifier = Modifier.width(100.dp)) {
                Icon(
                    vectorResource(id = R.drawable.ic_add_photo_alternate_24),
                    modifier = Modifier.preferredSize(36.dp)
                )
            }
        }

    }
}

@Composable
fun FollowedPodcasts(
    modifier: Modifier = Modifier,
    pagerState: PagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) { PagerState(clock) }
    },
) {
    pagerState.maxPage = (3 - 1).coerceAtLeast(0)

    Pager(
        state = pagerState,
        modifier = modifier
    ) {
        if(page == 0){ helloPage { pagerState.animateToNextPage() } }
        if(page == 1){ helloPage { pagerState.animateToNextPage() } }
        if(page == 2){ helloPage { pagerState.animateToLastPage() } }
    }
}

