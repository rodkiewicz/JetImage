package pl.mrodkiewicz.imageeditor.ui.splashscreen

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.DpPropKey
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.platform.AmbientConfiguration
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.helpers.getUriForCameraPhoto
import pl.mrodkiewicz.imageeditor.ui.actionBarTextStyle

enum class AnimationState { COLLAPSED, EXTENDED }

private val actionBarHeight = DpPropKey("ActionBarHeight =")
private val actionBarTextSize = FloatPropKey("TextSize")
private val madeByTextPadding = DpPropKey("TextSize")
private val bottomCardPadding = DpPropKey("Padding")

@Composable
fun SplashScreen(
    navController: NavController,
    takePhoto: ActivityResultLauncher<Uri>,
    getContent: ActivityResultLauncher<String>,
    splashScreenStateUI: MutableState<SplashScreenStateUI>
) {
    val actionBarExtendedHeight =
        with(AmbientDensity.current) { (AmbientConfiguration.current.screenHeightDp / 2).dp }

    val animation = remember {
        transitionDefinition<AnimationState>{
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
            transition(AnimationState.COLLAPSED to AnimationState.EXTENDED) {
                actionBarHeight using tween (
                    durationMillis = 1000
                )
                actionBarTextSize using tween (
                    durationMillis = 1000
                )
                madeByTextPadding using tween (
                    durationMillis = 1000
                )
                bottomCardPadding using tween (
                    durationMillis = 1000
                )
            }
        }
    }
    val (selected, onSelected) = remember { mutableStateOf(false) }

    // using on commit to fire animation when view is visible
    onCommit(callback = { if(splashScreenStateUI.value.fileSelected){onSelected(true)} })
    var animationBool = splashScreenStateUI.value.fileSelected
    val animationState = transition(
        definition = animation,
        toState = if (selected) AnimationState.EXTENDED else AnimationState.COLLAPSED,
        onStateChangeFinished = {
            navController.navigate("editorScreen")
            splashScreenStateUI.value = SplashScreenStateUI(false)
        }
    )

    Column {
        Surface(
            Modifier.fillMaxWidth().height(animationState[actionBarHeight])
                .background(MaterialTheme.colors.secondary)
        ) {
            Box(
                Modifier.background(MaterialTheme.colors.secondary).fillMaxSize()
                    .padding(start = 8.dp),
            ) {
                Text(
                    text = "JetPhoto",
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
                SplashViewPager(takePhoto = takePhoto, getContent = getContent).apply {
//                    pagerState.currentPage = splashScreenStateUI.value.currentPage TODO to implement
                }
            }
        }
    }
}

@Composable
fun HelloPage(nextPageButtonClick: () -> Unit) {
    Column(
        modifier = Modifier.background(Color(15, 15, 15)).fillMaxSize().padding(top = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Siema to jest \n najlepszy edytor bo",
            color = Color.LightGray,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
        Column(
            modifier = Modifier.padding(24.dp,4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            HelloPageItem(
                text = "lorem ipsum lorem ipsum lorem ipsum ",
                icon = vectorResource(id = R.drawable.ic_add_photo_alternate_24)
            )
            HelloPageItem(
                text = "lorem ipsum lorem ipsum lorem ipsum ",
                icon = vectorResource(id = R.drawable.ic_add_photo_alternate_24)
            )
            HelloPageItem(
                text = "lorem ipsum lorem ipsum lorem ipsum ",
                icon = vectorResource(id = R.drawable.ic_add_photo_alternate_24)
            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ){
                Button(
                    onClick = { nextPageButtonClick.invoke() },
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(text = "next page")
                }
            }

        }

    }
}

@Composable
fun HelloPageItem(text: String, icon: ImageVector){
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Surface(modifier = Modifier.preferredSize(48.dp), shape = RoundedCornerShape(4.dp)) {
            Icon(
                icon,
                modifier = Modifier.preferredSize(36.dp).background(Color(45,45,45))
            )
        }
        Text(
            modifier = Modifier.padding(8.dp, 8.dp),
            text = text,
            color = Color.LightGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Left
        )
    }
}

@Composable
fun PermissionPage(cameraPermissionButtonClick: () -> Unit, galleryPermissionButtonClick: () -> Unit) {
    Column(
        modifier = Modifier.background(Color(15, 15, 15)).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "permissions", color = Color.LightGray, fontSize = 28.sp)
        Spacer(Modifier.preferredSize(12.dp))
        Text(
            text = "musisz permissiony mi dac bo ",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
        Spacer(Modifier.preferredSize(8.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {cameraPermissionButtonClick.invoke()}, modifier = Modifier.width(100.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)) {
                    Icon(
                        vectorResource(id = R.drawable.ic_add_a_photo_24),
                        modifier = Modifier.preferredSize(36.dp)
                    )
                }
                Text(
                    text = "Camera permission",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp, 8.dp)
                )
            }
            Column(verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = {galleryPermissionButtonClick.invoke()}, modifier = Modifier.width(100.dp)) {
                    Icon(
                        vectorResource(id = R.drawable.ic_add_photo_alternate_24),
                        modifier = Modifier.preferredSize(36.dp)
                    )
                }
                Text(
                    text = "Gallery permission",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp, 8.dp)
                )
            }
        }

    }
}

@Composable
fun SelectImagePage(takePhoto: () -> Unit, getContent: () -> Unit) {
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
            Button(onClick = {takePhoto.invoke()}, modifier = Modifier.width(100.dp)) {
                Icon(
                    vectorResource(id = R.drawable.ic_add_a_photo_24),
                    modifier = Modifier.preferredSize(36.dp)
                )
            }
            Button(onClick = {getContent.invoke()}, modifier = Modifier.width(100.dp)) {
                Icon(
                    vectorResource(id = R.drawable.ic_add_photo_alternate_24),
                    modifier = Modifier.preferredSize(36.dp)
                )
            }
        }

    }
}

@Composable
fun SplashViewPager(
    modifier: Modifier = Modifier,
    takePhoto: ActivityResultLauncher<Uri>,
    getContent: ActivityResultLauncher<String>,
    pagerState: PagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) { PagerState(clock) }
    },
) {
    pagerState.maxPage = (2 - 1).coerceAtLeast(0)
    val context = AmbientContext.current
    Pager(
        state = pagerState,
        modifier = modifier
    ) {
        when (page) {
            0 -> HelloPage { pagerState.animateToNextPage() }
//            1 -> permissionPage( { {}} , { pagerState.animateToNextPage() })
            1 -> SelectImagePage ({ takePhoto.launch( getUriForCameraPhoto(context)) }, { getContent.launch("image/*")})
        }

    }
}

