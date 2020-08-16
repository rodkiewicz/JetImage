package pl.mrodkiewicz.imageeditor.data

import android.graphics.*
import timber.log.Timber


fun Bitmap.applyFilter(filter: Filter): Bitmap {
    val canvas = Canvas(this)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.reset()
    colorMatrix.setSaturation(0f)
    val matrix = filter.matrix
    colorMatrix.set(matrix)
    paint.colorFilter = ColorMatrixColorFilter(matrix)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return this
}

