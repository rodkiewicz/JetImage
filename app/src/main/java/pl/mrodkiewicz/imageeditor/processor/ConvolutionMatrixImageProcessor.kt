package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import androidx.renderscript.*
import pl.mrodkiewicz.imageeditor.data.AdjustFilter
import pl.mrodkiewicz.imageeditor.data.FilterType
import timber.log.Timber

class ConvolutionMatrixImageProcessor(
    val renderScript: RenderScript,
) {
     fun loadFilter(bitmap: Bitmap, adjustFilter: AdjustFilter): Bitmap =
        bitmap.applyConvolution(renderScript, adjustFilter)

}

fun Bitmap.applyConvolution(rs: RenderScript, adjustFilter: AdjustFilter): Bitmap {
    val newImage = this.copy(Bitmap.Config.ARGB_8888, true)
    val input = Allocation.createFromBitmap(rs, newImage)
    val output: Allocation = Allocation.createTyped(rs, input.type)
    var script: ScriptIntrinsic? = null
    when (adjustFilter.filterMatrix) {
        is FilterType.Convolve3x3 -> {
            Timber.d("image processor Convolve3x3")
            script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
            script.setCoefficients(
                adjustFilter.filterMatrix.calculateNewMatrix(
                    adjustFilter.filterMatrix.matrix,
                    adjustFilter.value
                )
            )
            script.setInput(input)
            repeat((adjustFilter.value / 10)) {
                (script as ScriptIntrinsicConvolve3x3?)?.forEach(input)
            }
            script.forEach(input)
            output.copyTo(newImage)
        }
        is FilterType.Convolve5x5 -> {
            Timber.d("image processor Convolve5x5")
            script = ScriptIntrinsicConvolve5x5.create(rs, Element.U8_4(rs))
            script.setCoefficients(
                adjustFilter.filterMatrix.calculateNewMatrix(
                    adjustFilter.filterMatrix.matrix,
                    adjustFilter.value
                )
            )
            script.setInput(input)
            repeat(adjustFilter.value) {
                script.forEach(input)
            }
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

