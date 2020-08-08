package pl.mrodkiewicz.imageeditor

import android.graphics.Bitmap
import android.graphics.Color
import java.util.*



var COLOR_MAX = 0xff

fun Bitmap.applyFilter(filter: Filter): Bitmap {
    val width = this.width
    val height = this.height
    val pixels = IntArray(width * height)
    this.getPixels(pixels, 0, width, 0, 0, width, height)

    val random = Random()

    var R: Int
    var G: Int
    var B: Int
    var index: Int
    for (y in 0 until height) {
        for (x in 0 until width) {
            index = y * width + x

            R = Color.red(pixels[index])
            G = Color.green(pixels[index])
            B = Color.blue(pixels[index])

            if(R < filter.red){
                R = 0
            }
//            if(G < filter.green){
//                G = 0
//            }
//            if(B < filter.blue){
//                B = 0
//            }
            pixels[index] = Color.rgb(R, G, B)
//            threshHold = random.nextInt(COLOR_MAX)
//            if (R > threshHold && G > threshHold && B > threshHold) {
//                pixels[index] = Color.rgb(COLOR_MAX, COLOR_MAX, COLOR_MAX)
//            }
        }
    }
    val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmapOut
}

