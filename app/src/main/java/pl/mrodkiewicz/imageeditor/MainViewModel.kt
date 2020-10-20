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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.blurImage
import pl.mrodkiewicz.imageeditor.data.default_filters
import timber.log.Timber

class MainViewModel @ViewModelInject constructor(val app: Application) : AndroidViewModel(app) {
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap
    private lateinit var originalBitmap: Bitmap
    private val _filters = MutableLiveData<MutableList<Filter>>(mutableListOf())
    val filters: LiveData<MutableList<Filter>> = _filters

    init {
        _filters.value = default_filters
    }


    fun updateFilter(index: Int, value: Float) {
        var oldList = _filters.value
        oldList!![index] =
            _filters.value!![index].copy(value = _filters.value!![index].value.plus(value.toInt()))
        _filters.value = oldList!!
        viewModelScope.launch {
            if (::originalBitmap.isInitialized) {
                val newBitmap =
                    loadFilterAsync(app, oldList[0].value)
                _bitmap.value = newBitmap
                Timber.d("bitmap: ${newBitmap.hashCode()}")
            }
        }
    }

    private suspend fun loadFilterAsync(app: Context, value : Int): Bitmap =
        withContext(Dispatchers.Default) {
            originalBitmap.blurImage(app, value)
        }

    fun setBitmap(bitmap: Bitmap?) {
        bitmap?.let {
            originalBitmap = it
        }
    }
}