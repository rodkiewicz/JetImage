package pl.mrodkiewicz.imageeditor

import android.content.Context
import android.graphics.Bitmap
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

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext val context: Context,
    val imageProcessorManager: ImageProcessorManager
) : ViewModel() {
    // UI State
    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap
    private val _filters = MutableStateFlow(default_filters)
    val filters: StateFlow<ImmutableList<Filter>> = _filters


    private var width = 0
    private var height = 0
    private lateinit var draftBitmap: Bitmap
    private lateinit var originalBitmap: Bitmap
    private val _filterPipeline: MutableStateFlow<Pair<ImmutableList<Filter>, Filter>> =
        MutableStateFlow(Pair(default_filters, default_filters[0]))


    init {
        viewModelScope.launch(Dispatchers.Main) {
            _filterPipeline.onEach { _filters.value = it.first }.debounce(250L).collect {
                imageProcessorManager.process(it)
            }
        }

    }


    fun updateFilter(index: Int, value: Int) {
        var list = _filters.value.toPersistentList().toMutableList()
        var newValue = list[index].value + value
        if (list[index].minValue <= newValue && list[index].maxValue >= newValue) {
            list[index] = list[index].copy(value = newValue)
            _filterPipeline.value = Pair(list.toImmutableList(), _filters.value[index])
        }
    }


    fun setBitmap(bitmap: Bitmap?) {
        bitmap?.let {
            originalBitmap = it
            draftBitmap = it
            _bitmap.value = it
            viewModelScope.launch(Dispatchers.Default) {
                imageProcessorManager.setBitmap(draftBitmap)
                withContext(Dispatchers.Main) {
                    _bitmap.value =
                        imageProcessorManager.process(_filters.value)
                }
            }
        }
    }

    fun setWidthAndHeigth(width: Int, height: Int) {
        if ((this.width != width || this.height != height) && (height != 0 && width != 0)) {
            this.width = width
            this.height = height
            imageProcessorManager.resizeImage()
            Timber.d("resize ${width} ${height}")
        }
    }



}