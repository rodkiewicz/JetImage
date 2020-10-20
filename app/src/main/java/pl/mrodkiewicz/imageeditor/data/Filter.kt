package pl.mrodkiewicz.imageeditor.data

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.renderscript.*
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.helpers.darkArray
import pl.mrodkiewicz.imageeditor.helpers.sepiaArray
import pl.mrodkiewicz.imageeditor.helpers.serPercentageForMatrix
import kotlin.random.Random

@Immutable
data class Filter(
    val name: String = "",
    var value: Int = 0,
    val icon: Int = 0,
    val customColor: Color = Color.Yellow,
    val maxValue: Int = 100,
    val minValue: Int = 0,
)

val default_filters = mutableListOf<Filter>(
    Filter(
        name = "Color",
        value = Random.nextInt(10) - 5,
        icon = R.drawable.ic_filter_24
    ),Filter(
        name = "Color",
        value = Random.nextInt(10) - 5,
        icon = R.drawable.ic_filter_24
    ),
    Filter(
        name = "Sepia",
        value = Random.nextInt(10) - 5,
        icon = R.drawable.ic_add_a_photo_24
    ),
    Filter(
        name = "Blur",
        value = Random.nextInt(10) - 5,
        icon = R.drawable.ic_reset_24
    ),
    Filter(
        name = "Sharpness",
        value = Random.nextInt(10) - 5,
        icon = R.drawable.ic_add_a_photo_24
    ),
    Filter(
        name = "Filtr 6",
        value = Random.nextInt(10) - 5,
        icon = R.drawable.ic_tune_24
    ),
)

fun Bitmap.blurImage(context: Context, value: Int): Bitmap {
    var rs = RenderScript.create(context)
    var bluredImage = this.copy(Bitmap.Config.ARGB_8888, true)
    var input = Allocation.createFromBitmap(rs, bluredImage)
    var output = Allocation.createTyped(rs, input.type)
    var script = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs))
    var matrix = Matrix3f(sepiaArray)
    matrix.serPercentageForMatrix(value)
    script.setColorMatrix(matrix)
    script.forEach(input, output)
    output.copyTo(bluredImage)
    rs.destroy()
    return bluredImage
}