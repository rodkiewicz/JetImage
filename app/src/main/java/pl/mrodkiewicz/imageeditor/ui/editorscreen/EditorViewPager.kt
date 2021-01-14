package pl.mrodkiewicz.imageeditor.ui.editorscreen

import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.gesture.Direction
import androidx.compose.ui.gesture.ScrollCallback
import androidx.compose.ui.gesture.scrollGestureFilter
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import pl.mrodkiewicz.imageeditor.ui.splashscreen.*
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * This is a modified version of:
 * https://github.com/android/compose-samples/blob/34a75fb3672622a3fb0e6a78adc88bbc2886c28f/Jetcaster/app/src/main/java/com/example/jetcaster/util/Pager.kt
 */

class EditorPagerState(
    clock: AnimationClockObservable,
    currentPage: Int = 0,
    minPage: Int = 0,
    maxPage: Int = 0
) {
    private var _minPage by mutableStateOf(minPage)
    var minPage: Int
        get() = _minPage
        set(value) {
            _minPage = value.coerceAtMost(_maxPage)
            _currentPage = _currentPage.coerceIn(_minPage, _maxPage)
        }

    private var _maxPage by mutableStateOf(maxPage, structuralEqualityPolicy())
    var maxPage: Int
        get() = _maxPage
        set(value) {
            _maxPage = value.coerceAtLeast(_minPage)
            _currentPage = _currentPage.coerceIn(_minPage, maxPage)
        }

    private var _currentPage by mutableStateOf(currentPage.coerceIn(minPage, maxPage))
    var currentPage: Int
        get() = _currentPage
        set(value) {
            _currentPage = value.coerceIn(minPage, maxPage)
        }


    var pageHeight = 0
    private var _currentOffset by mutableStateOf(0f)
    var currentOffset: Float
        get() = _currentOffset
        set(value) {
            _currentOffset = value.coerceIn((-pageHeight * maxPage).toFloat(), 0f)
        }
}


@Immutable
private data class EditorPageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@EditorPageData
}

private val Measurable.page: Int
    get() = (parentData as? EditorPageData)?.page ?: error("no PageData for measurable $this")

enum class AnimationEditorPagerState { HIDE, SCROLL, SHOW }

private val opacity = FloatPropKey("opacity")

@Composable
fun EditorPager(
        modifier: Modifier = Modifier,
        state: EditorPagerState,
        offscreenLimit: Int = 10,
        onValueChange: (Int, Float) -> Unit,
        visible: Boolean = false,
        pageContent: @Composable (EditorPagerScope.() -> Unit),
) {
    var pageSize by remember { mutableStateOf(0) }
    val (scroll, onScrollStatusChange) = remember { mutableStateOf(false) }
    val animation = remember {
        transitionDefinition<AnimationEditorPagerState> {
            state(AnimationEditorPagerState.HIDE) {
                this[opacity] = 0f
            }
            state(AnimationEditorPagerState.SHOW) {
                this[opacity] = 0.5f
            }
            state(AnimationEditorPagerState.SCROLL) {
                this[opacity] = 0.9f
            }
            transition(AnimationEditorPagerState.HIDE to AnimationEditorPagerState.SHOW) {
                opacity using tween(
                    durationMillis = 500
                )
            }
            transition(AnimationEditorPagerState.HIDE to AnimationEditorPagerState.SCROLL) {
                opacity using tween(
                    durationMillis = 500
                )
            }
            transition(AnimationEditorPagerState.SHOW to AnimationEditorPagerState.HIDE) {
                opacity using tween(
                    durationMillis = 500
                )
            }
            transition(AnimationEditorPagerState.SHOW to AnimationEditorPagerState.SCROLL) {
                opacity using tween(
                    durationMillis = 500
                )
            }
            transition(AnimationEditorPagerState.SCROLL to AnimationEditorPagerState.HIDE) {
                opacity using tween(
                    durationMillis = 500
                )
            }
            transition(AnimationEditorPagerState.SCROLL to AnimationEditorPagerState.SHOW) {
                opacity using tween(
                    durationMillis = 500
                )
            }
        }
    }
    val animationState = transition(
        definition = animation,
        toState = when {
            scroll -> AnimationEditorPagerState.SCROLL
            visible -> AnimationEditorPagerState.SHOW
            else -> AnimationEditorPagerState.HIDE
        },
    )
    Layout(
        content = {
            val minPage = (state.currentPage - offscreenLimit).coerceAtLeast(state.minPage)
            val maxPage = (state.currentPage + offscreenLimit).coerceAtMost(state.maxPage)

            for (page in minPage..maxPage) {
                val pageData = EditorPageData(page)
                val scope = EditorPagerScope(state, page)
                key(pageData) {
                    Box(contentAlignment = Alignment.Center, modifier = pageData) {
                        scope.pageContent()
                    }
                }
            }
        },

        modifier = modifier
            .alpha(animationState[opacity])
            .scrollGestureFilter(scrollCallback = object : ScrollCallback {
                override fun onStart(downPosition: Offset) {
                    onScrollStatusChange.invoke(true)
                    super.onStart(downPosition)
                }

                override fun onScroll(scrollDistance: Float): Float {
                    super.onScroll(scrollDistance)
                    onValueChange.invoke(state.currentPage, scrollDistance)
                    return scrollDistance
                }

                override fun onStop(velocity: Float) {
                    onScrollStatusChange.invoke(false)
                    super.onStop(velocity)
                }
            }, canDrag = { it == Direction.LEFT || it == Direction.RIGHT }, orientation = Orientation.Horizontal, startDragImmediately = false)
            .scrollGestureFilter(
                scrollCallback = object : ScrollCallback {
                    override fun onStart(downPosition: Offset) {
                        onScrollStatusChange.invoke(true)
                        super.onStart(downPosition)
                    }

                    override fun onScroll(scrollDistance: Float): Float {
                        super.onScroll(scrollDistance)
                        state.currentOffset += scrollDistance
                        return scrollDistance
                    }

                    override fun onStop(velocity: Float) {
                        onScrollStatusChange.invoke(false)
                        super.onStop(velocity)
                    }
                },
                canDrag = { it == Direction.UP || it == Direction.DOWN },
                orientation = Orientation.Vertical,
                startDragImmediately = false
            ),

        ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            val currentPage = state.currentPage
            val offset = state.currentOffset
            val childConstraints = constraints.copy(minWidth = 0, minHeight = 0)
            measurables
                .map {
                    it.measure(childConstraints) to it.page
                }
                .forEach { (placeable, page) ->
                    val xCenterOffset = (constraints.maxWidth - placeable.width) / 2
                    val yCenterOffset = (constraints.maxHeight - placeable.height) / 2
                    if (currentPage == page) {
                        pageSize = placeable.height
                        state.pageHeight = pageSize
                    }
                    placeable.place(
                        x = xCenterOffset,
                        y = (yCenterOffset + offset + (page * placeable.height)).roundToInt()
                    )
                    if ((yCenterOffset + offset + (page * placeable.height)).roundToInt() < constraints.maxHeight / 2) {
                        state.currentPage = page
                    }
                }
        }
    }
}

class EditorPagerScope(
        private val state: EditorPagerState,
        val page: Int
) {
    private val currentPage: Int
        get() = state.currentPage

    @Suppress("unused")
    fun Modifier.scalePagerItems(
    ): Modifier = Modifier.drawWithContent {
        drawContent()
    }.alpha(currentPage.distanceToOpacity(page))

}

fun Int.distanceToOpacity(page: Int): Float {
    return when (abs(this - page)) {
        0 -> return 1f
        else -> 0.5f
    }
}