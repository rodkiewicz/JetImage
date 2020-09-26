package pl.mrodkiewicz.imageeditor.data

import android.content.Context
import android.graphics.Bitmap
import androidx.renderscript.*


data class FilterUI(
    val name: String,
    val value: Int = 0,
    val icon: Int = 0,
    val customColor: Int = 0,
    val maxValue: Int = 0,
    val minValue: Int = 0,
)

fun Bitmap.blurImage(context: Context): Bitmap {
    var rs = RenderScript.create(context)
    var bluredImage = this.copy(Bitmap.Config.ARGB_8888, true)
    var input = Allocation.createFromBitmap(rs,bluredImage)
    var output = Allocation.createTyped(rs,input.type)
    var script = ScriptIntrinsicColorMatrix.create(rs, Element.U8_4(rs))
    script.setColorMatrix(
        Matrix3f(
            floatArrayOf(.393f, .349f, .272f,
        .769f, .686f, .534f,
        .189f, .168f, .131f)
        )
    )
    script.forEach(input,output)
    output.copyTo(bluredImage)
    this.recycle()
    rs.destroy()
    return bluredImage
}