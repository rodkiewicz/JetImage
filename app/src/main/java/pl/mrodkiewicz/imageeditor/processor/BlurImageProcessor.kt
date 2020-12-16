package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.FilterMatrix
import timber.log.Timber

class BlurImageProcessor(val renderScript: RenderScript) {
     fun loadBlur(bitmap: Bitmap, filter: Filter): Bitmap =
        bitmap.applyBlur(renderScript, filter)
}

fun Bitmap.applyBlur(rs: RenderScript, filter: Filter): Bitmap {
    val newImage = this.copy(this.config, true)
    val input = Allocation.createFromBitmap(rs, newImage)
    val output: Allocation = Allocation.createTyped(rs, input.type)
    var script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    var start = System.currentTimeMillis()
    Timber.d("applyBlur ${(filter.filterMatrix as FilterMatrix.Blur).validateValue(filter.value)}")
    script.setRadius(filter.filterMatrix.validateValue(filter.value))
    script.setInput(input)
    script.forEach(output)
    output.copyTo(newImage)
    input.destroy()
    output.destroy()
    Timber.d("applyBlur end time ${System.currentTimeMillis() - start}")

    return newImage
}