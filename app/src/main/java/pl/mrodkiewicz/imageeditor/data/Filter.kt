package pl.mrodkiewicz.imageeditor.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.renderscript.*
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.helpers.serPercentageForMatrix
import java.util.*
import kotlin.random.Random

@Immutable
data class Filter(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val value: Int = 0,
    val icon: Int = 0,
    val matrix: FloatArray = nonFilteredMatrix,
    val customColor: Color = Color.Yellow,
    val maxValue: Int = 100,
    val minValue: Int = 0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Filter

        if (name != other.name) return false
        if (value != other.value) return false
        if (icon != other.icon) return false
        if (!matrix.contentEquals(other.matrix)) return false
        if (customColor != other.customColor) return false
        if (maxValue != other.maxValue) return false
        if (minValue != other.minValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value
        result = 31 * result + icon
        result = 31 * result + matrix.contentHashCode()
        result = 31 * result + customColor.hashCode()
        result = 31 * result + maxValue
        result = 31 * result + minValue
        return result
    }
}
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
val sepiaMatrix =
    floatArrayOf(
        .393f, .349f, .272f,
        .769f, .686f, .534f,
        .189f, .168f, .131f
    )

val default_filters = mutableListOf(
    Filter(
        name = "Dark",
        value = 0,
        matrix = darkMatrix,
        icon = R.drawable.ic_filter_24
    ),Filter(
        name = "Sepia",
        matrix = sepiaMatrix,
        value = 0,
        icon = R.drawable.ic_filter_24
    ),
    Filter(
        name = "Sepia",
        value = 0,
        icon = R.drawable.ic_add_a_photo_24
    ),
    Filter(
        name = "Blur",
        value = 0,
        icon = R.drawable.ic_reset_24
    ),
    Filter(
        name = "Sharpness",
        value = 0,
        icon = R.drawable.ic_add_a_photo_24
    ),
    Filter(
        name = "Filtr 6",
        value = 0,
        icon = R.drawable.ic_tune_24
    ),
)




fun Bitmap.applyFilter(context: Context, filter: Filter): Bitmap {
    var rs = RenderScript.create(context)
    var newImage = this.copy(Bitmap.Config.ARGB_8888, true)
    var input = Allocation.createFromBitmap(rs, newImage)
    var output = Allocation.createTyped(rs, input.type)
    var script = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs))
    var matrix = Matrix3f(filter.matrix)
    matrix.serPercentageForMatrix(filter.value)
    script.setColorMatrix(matrix)
    script.forEach(input, output)
    output.copyTo(newImage)
    rs.destroy()
    return newImage
}