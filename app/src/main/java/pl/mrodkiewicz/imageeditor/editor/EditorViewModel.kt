package pl.mrodkiewicz.imageeditor.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ColorMatrix
import android.graphics.Matrix
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.mrodkiewicz.imageeditor.data.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

typealias ColorValue = Pair<VALUE_UPDATED, Float>

class EditorViewModel : ViewModel() {

    val filters = Channel<Filter>()
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap

    private lateinit var originalBitmap: Bitmap
    private var lastFilter = CustomFilter()

    init {
        viewModelScope.launch {
            filters.consumeAsFlow().collect {
                lastFilter = CustomFilter(it.matrix)
                val newBitmap =
                    loadFilterAsync(originalBitmap.copy(originalBitmap.config, true), it)
                _bitmap.value = newBitmap
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

    fun getFilterValues(section: SECTION): List<ColorValue> {
        return when (section) {
            SECTION.RED -> {
                return listOf(
                    Pair(VALUE_UPDATED.RED, lastFilter.matrix[0]),
                    Pair(VALUE_UPDATED.GREEN, lastFilter.matrix[1]),
                    Pair(VALUE_UPDATED.BLUE, lastFilter.matrix[2])
                )
            }
            SECTION.GREEN -> {
                return listOf(
                    Pair(VALUE_UPDATED.RED, lastFilter.matrix[5]),
                    Pair(VALUE_UPDATED.GREEN, lastFilter.matrix[6]),
                    Pair(VALUE_UPDATED.BLUE, lastFilter.matrix[7])
                )
            }
            SECTION.BLUE -> {
                return listOf(
                    Pair(VALUE_UPDATED.RED, lastFilter.matrix[10]),
                    Pair(VALUE_UPDATED.GREEN, lastFilter.matrix[11]),
                    Pair(VALUE_UPDATED.BLUE, lastFilter.matrix[12])
                )
            }
            SECTION.ALPHA -> {
                return listOf(
                    Pair(VALUE_UPDATED.RED, lastFilter.matrix[4]),
                    Pair(VALUE_UPDATED.GREEN, lastFilter.matrix[8]),
                    Pair(VALUE_UPDATED.BLUE, lastFilter.matrix[13])
                )
            }
        }
    }

    fun updateFilter(value: Float, section: SECTION, updated_value: VALUE_UPDATED) {
        filters.offer(
            lastFilter.update(
                FilterUtils().convertToMatrixIndex(section, updated_value),
                value
            )
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

    fun resetFilter() {
        val colorMatrix = ColorMatrix()
        colorMatrix.set(lastFilter.matrix)
        colorMatrix.reset()
        filters.offer(CustomFilter().apply { matrix = colorMatrix.array })
    }
}
