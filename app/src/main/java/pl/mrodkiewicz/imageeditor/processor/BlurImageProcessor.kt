package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import pl.mrodkiewicz.imageeditor.data.AdjustFilter
import pl.mrodkiewicz.imageeditor.data.FilterType

class BlurImageProcessor(val renderScript: RenderScript) {
     fun loadBlur(bitmap: Bitmap, adjustFilter: AdjustFilter): Bitmap =
        bitmap.applyBlur(renderScript, adjustFilter)
}

fun Bitmap.applyBlur(rs: RenderScript, adjustFilter: AdjustFilter): Bitmap {
    val newImage = this.copy(this.config, true)
    val input = Allocation.createFromBitmap(rs, newImage)
    val output: Allocation = Allocation.createTyped(rs, input.type)
    val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
    script.setRadius((adjustFilter.filterMatrix as FilterType.Blur).validateValue(adjustFilter.value))
    script.setInput(input)
    script.forEach(output)
    output.copyTo(newImage)
    input.destroy()
    output.destroy()
    return newImage
}