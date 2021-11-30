package pl.mrodkiewicz.imageeditor.ui.splashscreen

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.helpers.getUriForCameraPhoto
import pl.mrodkiewicz.imageeditor.ui.actionBarTextStyle

enum class AnimationState { COLLAPSED, EXTENDED }

@Composable
fun SplashScreen(
    navController: NavController,
    takePhoto: ActivityResultLauncher<Uri>,
    getContent: ActivityResultLauncher<String>,
    splashScreenStateUI: SplashScreenStateUI
) {
    val actionBarExtendedHeight =
        with(LocalDensity.current) { (LocalConfiguration.current.screenHeightDp / 2).dp }
    val transitionState = MutableTransitionState(AnimationState.EXTENDED)
    val transition =
        updateTransition(targetState = if (splashScreenStateUI.fileSelected) AnimationState.EXTENDED else AnimationState.COLLAPSED)
    val actionBarHeight by transition.animateDp({ tween(durationMillis = 1000) }) {
        when (it) {
            AnimationState.COLLAPSED -> actionBarExtendedHeight
            AnimationState.EXTENDED -> 56.dp
        }
    }
    val actionBarTextSize by transition.animateFloat({ tween(durationMillis = 1000) }) {
        when (it) {
            AnimationState.COLLAPSED -> 50f
            AnimationState.EXTENDED -> 32f
        }
    }
    val madeByTextPadding by transition.animateDp({ tween(durationMillis = 1000) }) {
        when (it) {
            AnimationState.COLLAPSED -> 8.dp
            AnimationState.EXTENDED -> 1000.dp
        }
    }
    val bottomCardPadding by transition.animateDp({ tween(durationMillis = 1000) }) {
        when (it) {
            AnimationState.COLLAPSED -> 16.dp
            AnimationState.EXTENDED -> 1000.dp
        }
    }
    val (selected, onSelected) = remember { mutableStateOf(false) }

    DisposableEffect(splashScreenStateUI) {
        if (splashScreenStateUI.fileSelected) {
            onSelected(true)
        }
        onDispose {
        }
    }
    
    if (transition.currentState == AnimationState.EXTENDED) {
        navController.navigate("editorScreen")
        splashScreenStateUI.fileSelected = false
    }
//    transition.targetState = AnimationState.COLLAPSED
//    val animationState = transition(
//        definition = animation,
//        toState = if (selected) AnimationState.EXTENDED else AnimationState.COLLAPSED,
//        onStateChangeFinished = {
//            navController.navigate("editorScreen")
//            splashScreenStateUI.value = SplashScreenStateUI(false)
//        }
//    )

    Column {
        Surface(
            Modifier
                .fillMaxWidth()
                .height(actionBarHeight)
                .background(MaterialTheme.colors.secondary)
        ) {
            Box(
                Modifier
                    .background(MaterialTheme.colors.secondary)
                    .fillMaxSize()
                    .padding(start = 8.dp),
            ) {
                Text(
                    text = "JetPhoto",
                    fontSize = actionBarTextSize.sp,
                    style = actionBarTextStyle,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = "Made by IT SUPER STAR",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic,
                    style = actionBarTextStyle,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp, madeByTextPadding, 8.dp, 8.dp)
                )

            }
        }
        Surface(modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()) {
            Card(
                shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp),
                modifier = Modifier
                    .padding(start = 16.dp, top = bottomCardPadding, end = 16.dp)
            ) {
                Button(onClick = { getContent.launch("image/*") }) {
                    Text(text = "1")
                }
//                SplashViewPager(takePhoto = takePhoto, getContent = getContent).apply {
////                    pagerState.currentPage = splashScreenStateUI.value.currentPage TODO to implement
//                }
            }
        }
    }
}

@Composable
fun HelloPage(nextPageButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(Color(15, 15, 15))
            .fillMaxSize()
            .padding(top = 12.dp),
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
            modifier = Modifier.padding(24.dp, 4.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

//            HelloPageItem(
//                text = "lorem ipsum lorem ipsum lorem ipsum ",
//                icon = vectorResource(id = R.drawable.ic_add_photo_alternate_24)
//            )
//            HelloPageItem(
//                text = "lorem ipsum lorem ipsum lorem ipsum ",
//                icon = vectorResource(id = R.drawable.ic_add_photo_alternate_24)
//            )
//            HelloPageItem(
//                text = "lorem ipsum lorem ipsum lorem ipsum ",
//                icon = vectorResource(id = R.drawable.ic_add_photo_alternate_24)
//            )
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
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

//@Composable
//fun HelloPageItem(text: String, icon: ImageVector) {
//    Row(
//        horizontalArrangement = Arrangement.Start,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
//    ) {
//        Surface(modifier = Modifier.preferredSize(48.dp), shape = RoundedCornerShape(4.dp)) {
//            Icon(
//                icon,
//                "",
//                modifier = Modifier.preferredSize(36.dp).background(Color(45, 45, 45))
//            )
//        }
//        Text(
//            modifier = Modifier.padding(8.dp, 8.dp),
//            text = text,
//            color = Color.LightGray,
//            fontSize = 12.sp,
//            textAlign = TextAlign.Left
//        )
//    }
//}

@Composable
fun PermissionPage(
    cameraPermissionButtonClick: () -> Unit,
    galleryPermissionButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(Color(15, 15, 15))
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "permissions", color = Color.LightGray, fontSize = 28.sp)
//        Spacer(Modifier.preferredSize(12.dp))
        Text(
            text = "musisz permissiony mi dac bo ",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
//        Spacer(Modifier.preferredSize(8.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Top,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { cameraPermissionButtonClick.invoke() },
                    modifier = Modifier.width(100.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.DarkGray)
                ) {
//                    Icon(
//                        vectorResource(id = R.drawable.ic_add_a_photo_24),
//                        "",
//                        modifier = Modifier.preferredSize(36.dp)
//                    )
                }
                Text(
                    text = "Camera permission",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp, 8.dp)
                )
            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { galleryPermissionButtonClick.invoke() },
                    modifier = Modifier.width(100.dp)
                ) {
//                    Icon(
//                        vectorResource(id = R.drawable.ic_add_photo_alternate_24),
//                        "",
//                        modifier = Modifier.preferredSize(36.dp)
//                    )
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
        modifier = Modifier
            .background(Color(15, 15, 15))
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Siema wybierz plik", color = Color.LightGray, fontSize = 28.sp)
//        Spacer(Modifier.preferredSize(12.dp))
        Text(
            text = "siema mordeczko wybierz plik z galerii czy tam aparatu jak wolisz sztywniuko mordo",
            color = Color.LightGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
//        Spacer(Modifier.preferredSize(8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Button(onClick = { takePhoto.invoke() }, modifier = Modifier.width(100.dp)) {
//                Icon(
//                    vectorResource(id = R.drawable.ic_add_a_photo_24),
//                    "",
//                    modifier = Modifier.preferredSize(36.dp)
//                )
            }
            Button(onClick = { getContent.invoke() }, modifier = Modifier.width(100.dp)) {
//                Icon(
//                    vectorResource(id = R.drawable.ic_add_photo_alternate_24),
//                    "",
//                    modifier = Modifier.preferredSize(36.dp)
//                )
            }
        }

    }
}

@Composable
fun SplashViewPager(
    modifier: Modifier = Modifier,
    takePhoto: ActivityResultLauncher<Uri>,
    getContent: ActivityResultLauncher<String>,
//    pagerState: PagerState = run {
//        remember { PagerState() }
//    },
) {
//    pagerState.maxPage = (2 - 1).coerceAtLeast(0)
//    val context = LocalContext.current
//    Pager(
//        state = pagerState,
//        modifier = modifier
//    ) {
//        when (page) {
//            0 -> HelloPage { pagerState.animateToNextPage() }
////            1 -> permissionPage( { {}} , { pagerState.animateToNextPage() })
//            1 -> SelectImagePage(
//                { takePhoto.launch(getUriForCameraPhoto(context)) },
//                { getContent.launch("image/*") })
//        }
//
//    }
}

