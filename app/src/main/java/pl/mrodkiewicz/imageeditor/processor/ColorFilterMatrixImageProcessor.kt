package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.renderscript.*
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.helpers.getPercentageFromOne
import pl.mrodkiewicz.imageeditor.helpers.getPercentageFromZero
import timber.log.Timber

class ColorFilterMatrixImageProcessor(
    val renderScript: RenderScript,
) {
     fun loadFilter(bitmap: Bitmap, filter: Filter): Bitmap =
        bitmap.applyFilters(renderScript, filter)
}

fun Bitmap.applyFilters(rs: RenderScript, filter: Filter): Bitmap {
    val newImage = this.copy(Bitmap.Config.ARGB_8888, true)
    val input = Allocation.createFromBitmap(rs, newImage)
    val output: Allocation = Allocation.createTyped(rs, input.type)
    var script = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs))
    var matrix = Matrix3f(filter.filterMatrix.matrix)
    matrix.serPercentageForMatrix(filter.value)
    Timber.d("image processor setColorMatrix")
    script.setColorMatrix(matrix)
    script.forEach(input, output)
    output.copyTo(newImage)
    input.destroy()
    output.destroy()
    return newImage
}

fun Matrix3f.serPercentageForMatrix(percentage: Int) {
    this.array[0] = this.array[0].getPercentageFromOne(percentage)
    this.array[1] = this.array[1].getPercentageFromZero(percentage)
    this.array[2] = this.array[2].getPercentageFromZero(percentage)
    this.array[3] = this.array[3].getPercentageFromZero(percentage)
    this.array[4] = this.array[4].getPercentageFromOne(percentage)
    this.array[5] = this.array[5].getPercentageFromZero(percentage)
    this.array[6] = this.array[6].getPercentageFromZero(percentage)
    this.array[7] = this.array[7].getPercentageFromZero(percentage)
    this.array[8] = this.array[8].getPercentageFromOne(percentage)
}