package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.renderscript.*
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.FilterMatrix
import pl.mrodkiewicz.imageeditor.data.setPercentage
import timber.log.Timber

class ConvolutionMatrixImageProcessor(
    val renderScript: RenderScript,
) {
    suspend fun loadFilter(bitmap: Bitmap, filter: Filter): Bitmap =
        bitmap.applyConvolution(renderScript, filter)

}

fun Bitmap.applyConvolution(rs: RenderScript, filter: Filter): Bitmap {
    val newImage = this.copy(Bitmap.Config.ARGB_8888, true)
    val input = Allocation.createFromBitmap(rs, newImage)
    val output: Allocation = Allocation.createTyped(rs, input.type)
    var script: ScriptIntrinsic? = null
    when (filter.filterMatrix) {
        is FilterMatrix.Convolve3x3 -> {
            Timber.d("image processor Convolve3x3")
            script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
            script.setCoefficients((filter.filterMatrix as FilterMatrix).setPercentage(filter.value))
            script.setInput(input)
//            repeat((filter.value/10).toInt()){
//                (script as ScriptIntrinsicConvolve3x3?)?.forEach(input)
//            }
            script.forEach(input)
            output.copyTo(newImage)
        }
        is FilterMatrix.Convolve5x5 -> {
            Timber.d("image processor Convolve5x5")
            script = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs))
            script.setCoefficients((filter.filterMatrix as FilterMatrix).matrix)
            script.setInput(input)
//            repeat(filter.value) {
                script.forEach(input)
//            }
            output.copyTo(newImage)
        }
        else -> Timber.d("wrong image processor")
    }
    input.destroy()
    output.destroy()
    return newImage
}

var BOX_3x3 = floatArrayOf(
    0.111f, 0.111f, 0.111f,
    0.111f, 0.111f, 0.111f,
    0.111f, 0.111f, 0.111f,
)
var BOX_5x = floatArrayOf(
    0.0030f, 0.0133f, 0.0219f, 0.0133f, 0.0030f,
    0.0133f, 0.0596f, 0.0983f, 0.0596f, 0.0133f,
    0.0219f, 0.0983f, 0.1621f, 0.0983f, 0.0219f,
    0.0133f, 0.0596f, 0.0983f, 0.0596f, 0.0133f,
    0.0030f, 0.0133f, 0.0219f, 0.0133f, 0.0030f
)

