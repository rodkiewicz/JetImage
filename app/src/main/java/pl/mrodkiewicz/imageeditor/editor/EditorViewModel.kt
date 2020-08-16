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
import pl.mrodkiewicz.imageeditor.data.CustomFilter
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.VALUE_UPDATED
import pl.mrodkiewicz.imageeditor.data.applyFilter
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class EditorViewModel : ViewModel() {

    private val filters = Channel<Filter>()
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap

    private lateinit var originalBitmap: Bitmap
    private var lastFilter = CustomFilter()

    init {
        viewModelScope.launch {
            filters.consumeAsFlow().collect {
                lastFilter = CustomFilter(it.matrix)
                val newBitmap = loadFilterAsync(originalBitmap.copy(originalBitmap.config, true), it)
                _bitmap.value = newBitmap
            }
        }
    }

    fun setBitmap(bitmap: Bitmap) {
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        var decoded = BitmapFactory.decodeStream(ByteArrayInputStream(out.toByteArray()))
        decoded = decoded.copy(decoded.config,true)
        originalBitmap = decoded
        _bitmap.value = decoded
    }


    fun updateFilter(value: Float, updated: VALUE_UPDATED) {
        when (updated) {
            VALUE_UPDATED.RED -> {
                filters.offer(lastFilter.updateRed(1 - value))
            }
            VALUE_UPDATED.BLUE -> {
                filters.offer(lastFilter.updateBlue(1 - value))

            }
            VALUE_UPDATED.GREEN -> {
                filters.offer(lastFilter.updateGreen(1 - value))
            }
            VALUE_UPDATED.HUE -> {
                filters.offer(lastFilter.updateRed(1 - value))
            }
        }
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
