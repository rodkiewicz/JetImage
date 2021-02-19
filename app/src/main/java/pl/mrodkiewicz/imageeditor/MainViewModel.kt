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
import pl.mrodkiewicz.imageeditor.di.MainImageProcessor
import pl.mrodkiewicz.imageeditor.processor.ImageProcessor
import pl.mrodkiewicz.imageeditor.processor.ImageProcessorManager


class MainViewModel @ViewModelInject constructor(
    @ApplicationContext val context: Context,
    @MainImageProcessor private val imageProcessor: ImageProcessor
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


    private val adjustFilterPipeline: MutableStateFlow<Pair<ImmutableList<AdjustFilter>, AdjustFilter>> =
        MutableStateFlow(Pair(default_adjust_filters, default_adjust_filters[0]))


    init {
        viewModelScope.launch(Dispatchers.Default) {

            adjustFilterPipeline.onEach { _filters.value = it.first }.debounce(25L).collect {
                imageProcessorManager.processAdjustFilter(it)

            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            imageProcessor.outputBitmap.collect {
                it?.let {
                    _bitmap.value = it
                }

            }
        }
        viewModelScope.launch(Dispatchers.Default) {
            imageProcessor.lutOutput.collect {
                _lut.value = it.toMutableList()
            }
        }
    }

    fun updateAdjustFilter(index: Int, value: Int) {
        val list = _filters.value.toPersistentList().toMutableList()
        val newValue = list[index].value + value
        if (list[index].minValue <= newValue && list[index].maxValue >= newValue) {
            list[index] = list[index].copy(value = newValue)
            adjustFilterPipeline.value = Pair(list.toImmutableList(), list[index])
        }
    }

    fun setLUTFilter(index: Int = -1, lutFilter: LutFilter) {
        viewModelScope.launch {
            _filters.value = default_adjust_filters
            if (index == _activeLutIndex.value) {
                imageProcessor.processLutFilter(null)
                _activeLutIndex.value = -1
            } else {
                _activeLutIndex.value = -1
                imageProcessor.processLutFilter(lutFilter)
                _activeLutIndex.value = index
            }

        }
    }

    fun setBitmapUri(uri: Uri) {
        viewModelScope.launch {

            imageProcessorManager.cleanup()
            imageProcessorManager.setBitmapUri(uri)

            // reset lut filter

            _activeLutIndex.value = -1
            // reset adjust filter pipeline
            adjustFilterPipeline.value = Pair(
                default_adjust_filters,
                default_adjust_filters[0]
            )
        }
    }

    fun setWidth(width: Int) {
        viewModelScope.launch {
            imageProcessor.setWidth(width)
        }
    }


    fun save(onSaved: (uri: Uri) -> Unit) {
        viewModelScope.launch {
            onSaved.invoke(
                imageProcessor.save()
            )
        }
    }

    override fun onCleared() {

        imageProcessorManager.cleanup()
        super.onCleared()

    }
}


