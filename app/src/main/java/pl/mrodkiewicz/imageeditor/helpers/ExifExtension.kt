package pl.mrodkiewicz.imageeditor.helpers

import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface

fun decodeExifOrientation(exifOrientation: Int): Matrix {
    val matrix = Matrix()
    when (exifOrientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1F, 1F)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1F, -1F)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postScale(-1F, 1F)
            matrix.postRotate(270F)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postScale(-1F, 1F)
            matrix.postRotate(90F)
        }
        else -> Unit
    }
    return matrix
}