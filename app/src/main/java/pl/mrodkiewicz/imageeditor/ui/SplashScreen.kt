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
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

private enum class AnimationState { START, END }

private val actionBarHeight = DpPropKey("ActionBarHeight =")
private val textSize = FloatPropKey("TextSize")
private val padding = DpPropKey("Padding")

@Composable
fun SplashScreen(navController: NavController) {
    WithConstraints {
        val animation = remember {
            transitionDefinition<AnimationState> {
                state(AnimationState.START) {
                    this[actionBarHeight] = (constraints.maxHeight/5).dp
                    this[textSize] = 50f
                    this[padding] = 16.dp
                }
                state(AnimationState.END) {
                    this[actionBarHeight] = 56.dp
                    this[textSize] = 32f
                    this[padding] = 1000.dp
                }
            }
        }
        val (selected, onSelected) = remember { mutableStateOf(false) }
        val selectionState = transition(
            definition = animation,
            toState = if (!selected) AnimationState.START else AnimationState.END
        )
        Column() {
            Surface(
                Modifier.fillMaxWidth().height(selectionState[actionBarHeight]).toggleable(value = selected, onValueChange = onSelected)
                    .background(MaterialTheme.colors.primary)
            ) {
                Row(
                    Modifier.background(MaterialTheme.colors.secondary)
                        .align(Alignment.CenterHorizontally).padding(start = 8.dp)
                ) {
                    Text(
                        text = "JetEditor",
                        fontSize = selectionState[textSize].sp,
                        style = actionBarTextStyle,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
            Surface(modifier = Modifier.fillMaxHeight().fillMaxWidth()) {
                Card(
                    shape = RoundedCornerShape(8.dp, 8.dp, 0.dp, 0.dp),
                    modifier = Modifier
                        .padding(start = 16.dp, top = selectionState[padding], end = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.background(Color(15, 15, 15)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Siema wybierz plik", color = Color.LightGray, fontSize = 28.sp )
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(0.5f).padding(top = 16.dp)){
                            Button(onClick = {}) {
                                Text(text = "APARAT", color = Color.DarkGray)
                            }
                            Button(onClick = {}) {
                                Text(text = "GALERIA", color = Color.DarkGray)

                            }
                        }

                    }
                }
            }
        }
    }
}


