package pl.mrodkiewicz.imageeditor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.res.imageResource
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.default_filters
import pl.mrodkiewicz.imageeditor.processor.ImageProcessorManager
import timber.log.Timber
import java.io.InputStream


class MainViewModel @ViewModelInject constructor(
    @ApplicationContext val context: Context,
    val imageProcessorManager: ImageProcessorManager
) : ViewModel() {
    // UI State
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap
    private val _filters = MutableStateFlow(default_filters)
    val filters: StateFlow<ImmutableList<Filter>> = _filters
    private var startTime = 0L

    private val _filterPipeline: MutableStateFlow<Pair<ImmutableList<Filter>, Filter>> =
        MutableStateFlow(Pair(default_filters, default_filters[0]))


    init {
        viewModelScope.launch(Dispatchers.Default) {
            _filterPipeline.onEach { _filters.value = it.first }.debounce(25L).collect {
                startTime = System.currentTimeMillis()
                imageProcessorManager.process(it)
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            imageProcessorManager.outputBitmap.collect {
                Timber.d("outputBitmap ${it.hashCode()}")
                Timber.d("time outputBitmap ${System.currentTimeMillis() - startTime}")
                it?.let {
                    withContext(Dispatchers.Main) {
                        _bitmap.value = it
                    }
                }
            }
        }
    }


    fun updateFilter(index: Int, value: Int) {
        var list = _filters.value.toPersistentList().toMutableList()
        var newValue = list[index].value + value
        if (list[index].minValue <= newValue && list[index].maxValue >= newValue) {
            list[index] = list[index].copy(value = newValue)
            _filterPipeline.value = Pair(list.toImmutableList(), list[index])
        }
    }

    fun setBitmapUri(uri: Uri) {
        viewModelScope.launch {
                imageProcessorManager.setBitmapUri(uri)
                _filterPipeline.value = Pair(default_filters, default_filters[0])
        }
    }

    fun setWidth(width: Int) {
        viewModelScope.launch {
            imageProcessorManager.setWidth(width)
        }
    }

    fun save(onSaved: (uri: Uri)->Unit){
        viewModelScope.launch {
            onSaved.invoke(imageProcessorManager.save(_filters.value))
            Timber.d("imageProcessorManager save ${_filters.value}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        imageProcessorManager.cleanup()
    }
}

