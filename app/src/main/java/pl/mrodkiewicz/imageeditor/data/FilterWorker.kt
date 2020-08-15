package pl.mrodkiewicz.imageeditor.data

import android.graphics.Bitmap
import android.graphics.Color
import timber.log.Timber


var COLOR_MAX = 0xff

fun Bitmap.applyFilter(filter: Filter): Bitmap {
    val width = this.width
    val height = this.height
    val pixels = IntArray(width * height)
    this.getPixels(pixels, 0, width, 0, 0, width, height)

    var r: Int
    var g: Int
    var b: Int
    var hue: Int
    var index: Int
    for (y in 0 until height) {
        for (x in 0 until width) {
            index = y * width + x

            r = Color.red(pixels[index])
            g = Color.green(pixels[index])
            b = Color.blue(pixels[index])
            val hsv = FloatArray(3)

            if(r < filter.red){
                r = 0
            }
            if(g < filter.green){
                g = 0
            }
            if(b < filter.blue){
                b = 0
            }
            pixels[index] = Color.rgb(r,g,b)
//            Color.RGBToHSV(r,g,b,hsv)
//            if(hsv[0] < 0.5){
//                hsv[0] = 0f
//            }
//            pixels[index] = Color.HSVToColor(hsv)
//            threshHold = random.nextInt(COLOR_MAX)
//            if (R > threshHold && G > threshHold && B > threshHold) {
//                pixels[index] = Color.rgb(COLOR_MAX, COLOR_MAX, COLOR_MAX)
//            }
        }
    }
    val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmapOut
}

