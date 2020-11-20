package pl.mrodkiewicz.imageeditor.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.toImmutableList
import pl.mrodkiewicz.imageeditor.R
import java.util.*

@Immutable
data class Filter(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val value: Int = 0,
    val icon: Int = 0,
    val filterMatrix: FilterMatrix = FilterMatrix.ColorFilter(nonFilteredMatrix),
    val customColor: Color = Color.Yellow,
    val maxValue: Int = 100,
    val minValue: Int = 0,
)

sealed class FilterMatrix(val matrix: FloatArray) {
    data class ColorFilter(var colorMatrix: FloatArray): FilterMatrix(colorMatrix)
    data class Convolve3x3(val convolveMatrix: FloatArray) : FilterMatrix(convolveMatrix)
    data class Convolve5x5(val convolveMatrix: FloatArray) : FilterMatrix(convolveMatrix)

}
fun FilterMatrix.setPercentage(percentage : Int): FloatArray{
    return when(this){
        is FilterMatrix.ColorFilter -> this.matrix.serPercentageForMatrix(percentage)
        is FilterMatrix.Convolve3x3 -> this.matrix
        is FilterMatrix.Convolve5x5 -> this.matrix
    }
}
var convolutionMatrix1 = floatArrayOf(
    0.000000000001f, 0.000000000001f,0.000000000001f,
    0.000000000001f, 0.000000000001f, 0.000000000001f,
    0.000000000001f, 0.000000000001f, 0.000000000001f,

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

//if percentage is 0 the filter intensity is 0
fun FloatArray.serPercentageForMatrix(percentage: Int): FloatArray {
    var floatArray = this
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

val default_filters = mutableListOf(
    Filter(
        name = "Convolution",
        value = 0,
        filterMatrix = FilterMatrix.Convolve3x3(convolutionMatrix1),
        icon = R.drawable.ic_filter_24
    ), Filter(
        name = "Dark",
        value = 0,
        filterMatrix = FilterMatrix.ColorFilter(darkMatrix),
        icon = R.drawable.ic_filter_24
    )
).toImmutableList()