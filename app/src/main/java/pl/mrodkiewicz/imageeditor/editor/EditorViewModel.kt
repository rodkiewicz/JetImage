package pl.mrodkiewicz.imageeditor.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import pl.mrodkiewicz.imageeditor.data.VALUE_UPDATED
import pl.mrodkiewicz.imageeditor.data.applyFilter
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class EditorViewModel : ViewModel() {

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap
    val filters = Channel<Filter>()

    private lateinit var originalBitmap: Bitmap
    private var lastFilter = Filter()

    init {
        viewModelScope.launch {
            filters.consumeAsFlow().collect {
                lastFilter = it
                var newBitmap = loadFilterAsync(originalBitmap, it)
                _bitmap.value = newBitmap
            }
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 6, out)
        val decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
        originalBitmap = decoded
        _bitmap.value = decoded
    }

    fun updateFilter(value: Int, updated: VALUE_UPDATED) {
        when (updated) {
            VALUE_UPDATED.RED -> {
                filters.offer(lastFilter.copy(red = value))
            }
            VALUE_UPDATED.BLUE -> {
                filters.offer(lastFilter.copy(blue = value))

            }
            VALUE_UPDATED.GREEN -> {
                filters.offer(lastFilter.copy(green = value))

            }
            VALUE_UPDATED.HUE -> {
                filters.offer(lastFilter.copy(hue = value))

            }
        }
    }

    private suspend fun loadFilterAsync(originalBitmap: Bitmap, filter: Filter): Bitmap =
        withContext(Dispatchers.Default) {
            originalBitmap.applyFilter(filter)
        }
}
