package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import androidx.renderscript.*
import pl.mrodkiewicz.imageeditor.data.LutFilter

class LutImageProcessor(
    val renderScript: RenderScript,
) {
    fun loadFilter(bitmap: Bitmap, lutFilter: LutFilter): Bitmap =
        bitmap.applyLut(renderScript, lutFilter)
}

fun Bitmap.applyLut(rs: RenderScript, lutFilter: LutFilter): Bitmap {
    val newImage = this.copy(Bitmap.Config.ARGB_8888, true)
    val input = Allocation.createFromBitmap(rs, newImage)
    val output: Allocation = Allocation.createTyped(rs, input.type)
    val script = ScriptIntrinsic3DLUT.create(rs, Element.U8_4(rs))
    val type = Type.Builder(rs, Element.U8_4(rs)).apply {
        setX(lutFilter.x)
        setY(lutFilter.z)
        setZ(lutFilter.y)
    }.create()
    val lut = Allocation.createTyped(rs,type)
    lut.copyFromUnchecked(lutFilter.lutFilter)
    script.setLUT(lut)
    script.forEach(input, output)

    output.copyTo(newImage)
    input.destroy()
    output.destroy()

    return newImage
}

