package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import androidx.renderscript.*
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.setPercentage
import timber.log.Timber

class ColorFilterMatrixImageProcessor(
    val renderScript: RenderScript,
) {
    suspend fun loadFilter(bitmap: Bitmap,filter: Filter): Bitmap =
        bitmap.applyFilters(renderScript, filter)
}

fun Bitmap.applyFilters(rs: RenderScript, filter: Filter): Bitmap {
    val newImage = this.copy(Bitmap.Config.ARGB_8888, true)
    var input = Allocation.createFromBitmap(rs, newImage)
    var output = Allocation.createTyped(rs, input.type)
    val script = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs))
    var matrix = Matrix3f((filter.filterMatrix).setPercentage(filter.value))
    script.setColorMatrix(matrix)
    script.forEach(input, output)
    output.copyTo(newImage)
    input = Allocation.createFromBitmap(rs, newImage)
    output = Allocation.createTyped(rs, input.type)
    Timber.d("applyFilters ${filter.name} ${filter.value}")
    input.destroy()
    output.destroy()
    return newImage
}