package pl.mrodkiewicz.imageeditor.ui

import androidx.compose.animation.animate
import androidx.compose.animation.core.AnimationClockObservable
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.gestures.rememberScrollableController
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.gesture.doubleTapGestureFilter
import androidx.compose.ui.gesture.longPressGestureFilter
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.unit.Density
import timber.log.Timber
import kotlin.math.roundToInt


/**
 * This is a modified version of:
 * https://github.com/android/compose-samples/blob/34a75fb3672622a3fb0e6a78adc88bbc2886c28f/Jetcaster/app/src/main/java/com/example/jetcaster/util/Pager.kt
 */

class PagerState(
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
    var pageWidth = 0
    private var _currentOffset by mutableStateOf(0f)
    var currentOffset: Float
        get() = _currentOffset
        set(value) {
            _currentOffset = value.coerceIn((-pageWidth * maxPage).toFloat(), 0f)
        }
}


@Immutable
private data class PageData(val page: Int) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any? = this@PageData
}

private val Measurable.page: Int
    get() = (parentData as? PageData)?.page ?: error("no PageData for measurable $this")

@Composable
public fun Pager(
    state: PagerState,
    offscreenLimit: Int = 10,
    modifier: Modifier = Modifier,
    pageContent: @Composable PagerScope.() -> Unit
) {
    var pageSize by remember { mutableStateOf(0) }
    val visibility = remember { mutableStateOf(true) }
    val opacity = animate(if (visibility.value) 0f else 1f)
    Layout(
        children = {
            val minPage = (state.currentPage - offscreenLimit).coerceAtLeast(state.minPage)
            val maxPage = (state.currentPage + offscreenLimit).coerceAtMost(state.maxPage)

            for (page in minPage..maxPage) {
                val pageData = PageData(page)
                val scope = PagerScope(state, page)
                key(pageData) {
                    Box(gravity = ContentGravity.Center, modifier = pageData) {
                        scope.pageContent()
                    }
                }
            }
        },

        modifier = modifier
            .doubleTapGestureFilter {
                visibility.value = !visibility.value
            }
            .longPressGestureFilter {
                visibility.value = !visibility.value
            }
            .drawOpacity(opacity = opacity)
            .scrollable(Orientation.Horizontal, rememberScrollableController {
                state.currentOffset += it
                it
            }).scrollable(
                orientation = Orientation.Vertical, rememberScrollableController {
                    Timber.d("scroll ${it}")
                    it
                }
            )


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
                        pageSize = placeable.width
                        state.pageWidth = pageSize
                    }
                    placeable.place(
                        x = (xCenterOffset + offset + (page * placeable.width)).roundToInt(),
                        y = yCenterOffset
                    )
                    if ((xCenterOffset + offset + (page * placeable.width)).roundToInt() < constraints.maxWidth / 2) {
                        state.currentPage = page
                    }
                }
        }
    }
}

class PagerScope(
    private val state: PagerState,
    val page: Int
) {
    val currentPage: Int
        get() = state.currentPage

    fun Modifier.scalePagerItems(
    ): Modifier = Modifier.drawWithContent {
        drawContent()
    }.drawOpacity(if (currentPage == page) 1f else 0.7f)

}