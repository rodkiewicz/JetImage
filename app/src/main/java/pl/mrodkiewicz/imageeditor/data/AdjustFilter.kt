package pl.mrodkiewicz.imageeditor.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.toImmutableList
import pl.mrodkiewicz.imageeditor.R
import java.util.*

@Immutable
data class AdjustFilter(
        val id: UUID = UUID.randomUUID(),
        val name: String = "",
        val value: Int = 0,
        val icon: Int = 0,
        val filterMatrix: FilterType = FilterType.ColorFilter(nonFilteredMatrix),
        val customColor: Color = Color.Yellow,
        val maxValue: Int = 100,
        val minValue: Int = 0,
)

sealed class FilterType(val matrix: FloatArray) {
    data class ColorFilter(
            var colorMatrix: FloatArray,
            var calculateNewMatrix: (FloatArray, Int) -> FloatArray = { matrix, update ->
                matrix.serPercentageForMatrix(update)
            }
    ) : FilterType(colorMatrix)

    data class Convolve3x3(
            val convolveMatrix: FloatArray,
            var calculateNewMatrix: (FloatArray, Int) -> FloatArray = { matrix, update ->
                matrix
            }
    ) : FilterType(convolveMatrix)

    data class Convolve5x5(
            val convolveMatrix: FloatArray,
            var calculateNewMatrix: (FloatArray, Int) -> FloatArray = { matrix, update ->
                matrix
            }
    ) : FilterType(convolveMatrix)

    data class Blur(
            var validateValue: (Int) -> Float = { value ->
                (value / 100f * 25f).coerceAtLeast(1f).coerceAtMost(25f)
            }
    ) : FilterType(nonFilteredMatrix)

}

var convolutionMatrix1 = floatArrayOf(
        0f, -1f, 0f,
        -1f, 5f, -1f,
        0f, -1f, 0f,
)
var convolutionMatrix2 = floatArrayOf(
        1f / 256f, 4f / 256f, 6f / 256f, 4f / 256f, 1f / 256f,
        4f / 256f, 16f / 256f, 24f / 256f, 16f / 256f, 4f / 256f,
        6f / 256f, 24f / 256f, 36f / 256f, 24f / 256f, 6f / 256f,
        4f / 256f, 16f / 256f, 24f / 256f, 16f / 256f, 4f / 256f,
        1f / 256f, 4f / 256f, 6f / 256f, 4f / 256f, 1f / 256f
)
val nonFilteredMatrix =
        floatArrayOf(
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f, 0f, 1f
        )

val darkMatrix =
        floatArrayOf(
                0f, 0f, 0f,
                0f, 0f, 0f,
                0f, 0f, 0f
        )
val lightMatrix =
        floatArrayOf(
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f
        )
val sepiaMatrix =
        floatArrayOf(
                .393f, .349f, .272f,
                .769f, .686f, .534f,
                .189f, .168f, .131f
        )
val redMatrix =
        floatArrayOf(
                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f
        )
val greenMatrix =
        floatArrayOf(
                0f, 1f, 0f,
                0f, 1f, 0f,
                0f, 1f, 0f
        )

val blueMatrix =
        floatArrayOf(
                0f, 0f, 1f,
                0f, 0f, 1f,
                0f, 0f, 1f
        )


//if percentage is 0 the filter intensity is 0
fun FloatArray.serPercentageForMatrix(percentage: Int): FloatArray {
    val floatArray = this
    floatArray[0] = floatArray[0].getPercentageFromOne(percentage)
    floatArray[1] = floatArray[1].getPercentageFromZero(percentage)
    floatArray[2] = floatArray[2].getPercentageFromZero(percentage)
    floatArray[3] = floatArray[3].getPercentageFromZero(percentage)
    floatArray[4] = floatArray[4].getPercentageFromOne(percentage)
    floatArray[5] = floatArray[5].getPercentageFromZero(percentage)
    floatArray[6] = floatArray[6].getPercentageFromZero(percentage)
    floatArray[7] = floatArray[7].getPercentageFromZero(percentage)
    floatArray[8] = floatArray[8].getPercentageFromOne(percentage)
    return floatArray
}

//if percentage is 0 the  float value is 1, if percentage is 100 the float value is float
fun Float.getPercentageFromOne(percentage: Int): Float {
    return this + ((1f - this) * ((100f - percentage) / 100f))
}

//if percentage is 0 the  float value is 0, if percentage is 100 the float value is float
fun Float.getPercentageFromZero(percentage: Int): Float {
    return (this / 100 * percentage)
}

val default_adjust_filters = mutableListOf(
        AdjustFilter(
                name = "Red",
                filterMatrix = FilterType.ColorFilter(redMatrix),
                icon = R.drawable.ic_filter_24
        ),
        AdjustFilter(
                name = "Green",
                filterMatrix = FilterType.ColorFilter(greenMatrix),
                icon = R.drawable.ic_filter_24
        ),
        AdjustFilter(
                name = "Blue",
                filterMatrix = FilterType.ColorFilter(blueMatrix),
                icon = R.drawable.ic_filter_24
        ),
        AdjustFilter(
                name = "Dark",
                filterMatrix = FilterType.ColorFilter(darkMatrix),
                icon = R.drawable.ic_filter_24
        ),
        AdjustFilter(
                name = "Light",
                filterMatrix = FilterType.ColorFilter(lightMatrix),
                icon = R.drawable.ic_filter_24
        ),
        AdjustFilter(
                name = "Sepia",
                filterMatrix = FilterType.ColorFilter(sepiaMatrix),
                icon = R.drawable.ic_filter_24
        ),
        AdjustFilter(
                name = "Blur",
                value = 1,
                filterMatrix = FilterType.Blur(),
                icon = R.drawable.ic_filter_24
        ),
).toImmutableList()