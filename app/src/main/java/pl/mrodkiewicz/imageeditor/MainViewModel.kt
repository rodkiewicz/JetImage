package pl.mrodkiewicz.imageeditor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import pl.mrodkiewicz.imageeditor.data.AdjustFilter
import pl.mrodkiewicz.imageeditor.data.LutFilter
import pl.mrodkiewicz.imageeditor.data.default_adjust_filters
import pl.mrodkiewicz.imageeditor.data.default_lut_filters
import pl.mrodkiewicz.imageeditor.processor.ImageProcessorManager
import timber.log.Timber


class MainViewModel @ViewModelInject constructor(
    @ApplicationContext val context: Context,
    val imageProcessorManager: ImageProcessorManager
) : ViewModel() {
    // UI State
    private val _bitmap = MutableStateFlow<Bitmap?>(null)
    val bitmap: StateFlow<Bitmap?> = _bitmap
    private val _filters = MutableStateFlow(default_adjust_filters)
    val filters: StateFlow<ImmutableList<AdjustFilter>> = _filters
    private val _lut: MutableStateFlow<MutableList<LutFilter>> = MutableStateFlow(
        default_lut_filters.toMutableList()
    )
    val lut: StateFlow<List<LutFilter>> = _lut
    private val _activeLutIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    val activeLutIndex: StateFlow<Int> = _activeLutIndex


    private var startTime = 0L
    private val _adjust_filter_pipeline: MutableStateFlow<Pair<ImmutableList<AdjustFilter>, AdjustFilter>> =
        MutableStateFlow(Pair(default_adjust_filters, default_adjust_filters[0]))


    init {
        viewModelScope.launch(Dispatchers.Default) {
            _adjust_filter_pipeline.onEach { _filters.value = it.first }.debounce(25L).collect {
                startTime = System.currentTimeMillis()
                imageProcessorManager.processAdjustFilter(it)
            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            imageProcessorManager.outputBitmap.collect {
                it?.let {
                    _bitmap.value = it
                }

            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            imageProcessorManager.lutOutput.collect {
                _lut.value = it.toMutableList()
            }
        }

    }


    fun updateAdjustFilter(index: Int, value: Int) {
        var list = _filters.value.toPersistentList().toMutableList()
        var newValue = list[index].value + value
        if (list[index].minValue <= newValue && list[index].maxValue >= newValue) {
            list[index] = list[index].copy(value = newValue)
            _adjust_filter_pipeline.value = Pair(list.toImmutableList(), list[index])
        }
    }

    fun setLUTFilter(index: Int = -1, lutFilter: LutFilter) {
        viewModelScope.launch {
            _filters.value = default_adjust_filters
            if (index == _activeLutIndex.value) {
                imageProcessorManager.processLutFilter(null)
                _activeLutIndex.value = -1
            } else {
                _activeLutIndex.value = -1
                imageProcessorManager.processLutFilter(lutFilter)
                _activeLutIndex.value = index
            }

        }
    }

    fun setBitmapUri(uri: Uri) {
        viewModelScope.launch {
            imageProcessorManager.setBitmapUri(uri)
            _activeLutIndex.value = -1
            _adjust_filter_pipeline.value = Pair(
                default_adjust_filters,
                default_adjust_filters[0]
            )
        }
    }

    fun setWidth(width: Int) {
        viewModelScope.launch {
            imageProcessorManager.setWidth(width)
        }
    }


    fun save(onSaved: (uri: Uri) -> Unit) {
        viewModelScope.launch {
            onSaved.invoke(
                imageProcessorManager.save()
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        imageProcessorManager.cleanup()
    }
}


