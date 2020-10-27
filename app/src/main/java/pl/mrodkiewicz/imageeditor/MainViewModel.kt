package pl.mrodkiewicz.imageeditor

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.applyFilter
import pl.mrodkiewicz.imageeditor.data.default_filters
import pl.mrodkiewicz.imageeditor.data.sepiaMatrix
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(val app: Application) : AndroidViewModel(app) {
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap
    private lateinit var originalBitmap: Bitmap
    private val _filters = default_filters
    private val _filter = ConflatedBroadcastChannel<Filter>()
    val filters: StateFlow<List<Filter>>


    init {
        filters = MutableStateFlow(default_filters)
        _filter.asFlow().debounce(250L).onEach {
            if (::originalBitmap.isInitialized) {
                val newBitmap =
                    loadFilterAsync(it)
                _bitmap.value = newBitmap
                Timber.d("newBitmap: ${newBitmap.hashCode()}")
            }
        }.launchIn(viewModelScope)
    }


    fun updateFilter(index: Int, value: Int) {
        var newValue = _filters[index].value + value
        if (_filters[index].minValue <= newValue && _filters[index].maxValue >= newValue) {
            viewModelScope.launch {
                _filters[index] = _filters[index].copy(value = newValue)
                _filter.send(_filters[index])
            }
        }
    }

    private suspend fun loadFilterAsync(filter: Filter): Bitmap =
        withContext(Dispatchers.Default) {
            originalBitmap.applyFilter(app, filter)
        }

    fun setBitmap(bitmap: Bitmap?) {
        bitmap?.let {
            Timber.d("setBitmap: ${bitmap.hashCode()}")
            originalBitmap = it
            viewModelScope.launch {
            val newBitmap =
                loadFilterAsync(Filter(value = 50, matrix = sepiaMatrix))
                _bitmap.value = newBitmap
            }
        }
    }
}