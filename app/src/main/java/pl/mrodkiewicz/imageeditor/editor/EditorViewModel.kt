package pl.mrodkiewicz.imageeditor.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.mrodkiewicz.imageeditor.data.Filter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class EditorViewModel : ViewModel() {

    val filters = Channel<Filter>()
    private val _bitmap = MutableLiveData<Bitmap>()
    private val _filter = MutableLiveData<Filter>()
    val bitmap: LiveData<Bitmap> = _bitmap
    val filter: LiveData<Filter> = _filter
    private var originalBitmap: Bitmap

    init {
        originalBitmap = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888)
        viewModelScope.launch {
            filters.consumeAsFlow().collect {
                val newBitmap =
                    loadFilterAsync(originalBitmap,it)
                _bitmap.value = newBitmap
                _filter.value = it
            }
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        var decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
        decoded = decoded.copy(decoded.config, true)
        originalBitmap = decoded
        _bitmap.value = decoded
    }


    fun updateFilter(newFilter: Filter) {
        filters.offer(
            newFilter
        )
    }

    fun resetFilter(){
        filters.offer(
            Filter(red = 200, green = 10, hue = 1)
        )
    }

    private suspend fun loadFilterAsync(originalBitmap: Bitmap, filter: Filter): Bitmap =
        withContext(Dispatchers.Default) {
            originalBitmap.applyFilter(filter)
        }


    override fun onCleared() {
        super.onCleared()
        originalBitmap.recycle()
        bitmap.value?.recycle()
    }


}
fun Bitmap.applyFilter(filter: Filter): Bitmap {
    val width = this.width
    val height = this.height
    val pixels = IntArray(width * height)
    this.getPixels(pixels, 0, width, 0, 0, width, height)
    var index: Int
    for (y in 0 until height) {
        for (x in 0 until width) {
            index = y * width + x
            var r = Color.red(pixels[index])
            var g = Color.green(pixels[index])
            var b = Color.blue(pixels[index])
            var hsv = floatArrayOf(0F,0F, 0F)
            Color.RGBToHSV(r,g,b,hsv)

        }
    }
    val bitmapOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    bitmapOut.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmapOut
}


