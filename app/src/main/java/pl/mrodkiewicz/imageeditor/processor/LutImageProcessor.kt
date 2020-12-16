package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.renderscript.*
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.FilterMatrix
import timber.log.Timber

class LutImageProcessor(
    val renderScript: RenderScript,
) {
    fun loadFilter(bitmap: Bitmap, filter: Filter): Bitmap =
        bitmap.applyLut(renderScript, filter)

}

fun Bitmap.applyLut(rs: RenderScript, filter: Filter): Bitmap {
    val newImage = this.copy(Bitmap.Config.ARGB_8888, true)
    val input = Allocation.createFromBitmap(rs, newImage)
    val output: Allocation = Allocation.createTyped(rs, input.type)
    var script = ScriptIntrinsicLUT.create(rs, Element.U8_4(rs))
    var matrix = Matrix3f(filter.filterMatrix.matrix)
    script.forEach(input, output)
    output.copyTo(newImage)
    Timber.d("applyFilters ${filter.name} ${filter.value}")
    Timber.d(
        "bitmap value r ${Color(newImage.getPixel(100, 100)).red} g ${
            Color(
                newImage.getPixel(
                    100,
                    100
                )
            ).green
        } b ${Color(newImage.getPixel(100, 100)).blue}"
    )

    input.destroy()
    output.destroy()
    return newImage
}

