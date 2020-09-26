package pl.mrodkiewicz.imageeditor.data

import android.graphics.*
import kotlinx.coroutines.channels.Channel
import timber.log.Timber


//
//
//fun Bitmap.applyFilter(filter: FilterUI, progress: Channel<Int>): Bitmap {
//    val width = this.width
//    val height = this.height
//    val pixels = IntArray(width * height)
//    val totalIndex = width*height
//    var step = totalIndex / 100
//    var _progress = 0
//    this.getPixels(pixels, 0, width, 0, 0, width, height)
//    var index: Int
//    for (y in 0 until height) {
//        for (x in 0 until width) {
//            index = y * width + x
//            var r = Color.red(pixels[index])
//            var g = Color.green(pixels[index])
//            var b = Color.blue(pixels[index])
//            var hsv = floatArrayOf(0f,0f,0f)
//            Color.colorToHSV(pixels[index],hsv)
//            if(r > filter.red) r = 0
//            if(g > filter.green) g = 0
//            if(b > filter.blue) b = 0
//            pixels[index] = Color.rgb(r,g,b)
//            if(index == step){
//                progress.offer(++_progress)
//                step += totalIndex/100
//            }
//        }
//    }
//    val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
//    bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
//    return bitmapOut
//}

