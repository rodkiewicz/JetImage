package pl.mrodkiewicz.imageeditor

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
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
import pl.mrodkiewicz.imageeditor.helpers.saveImage
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
        viewModelScope.launch(Dispatchers.Default) {
            _filterPipeline.onEach { _filters.value = it.first }.debounce(250L).collect {
                if (width != 0) {
                    var newBitmap = imageProcessorManager.process(it)
                    newBitmap.saveImage(context, "bitmaps", "${_filters.value[0].value}")
                    Timber.d(
                        "bitmap value r ${Color(newBitmap.getPixel(100, 100)).red} g ${
                            Color(
                                newBitmap.getPixel(100, 100)
                            ).green
                        } b ${Color(newBitmap.getPixel(100, 100)).blue}"

                    )
                    withContext(Dispatchers.Main) {
                        _bitmap.value = newBitmap
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
            _filterPipeline.value = Pair(list.toImmutableList(), _filters.value[index])
        }
    }


    fun setBitmap(bitmap: Bitmap?) {
        bitmap?.let {
            originalBitmap = it
//            bitmap.saveImage(context, "bitmaps", "startBitamp_${bitmap.height}")
            draftBitmap = it
            viewModelScope.launch(Dispatchers.Default) {
                imageProcessorManager.setBitmap(draftBitmap)
                if (width != 0) {
                    Timber.d("setBitmap width != 0")
                    withContext(Dispatchers.Default) {
                        _bitmap.value =
                            imageProcessorManager.process(_filters.value)
                    }
                } else {
                    Timber.d("setBitmap width == 0")
                }
            }
        }
    }

    fun setWidth(width: Int) {
        Timber.d("setBitmap width ${width}")
        if ((this.width != width) && (width != 0) && draftBitmap.width != this.width && ::originalBitmap.isInitialized) {
            this.width = width
            viewModelScope.launch {
                withContext(Dispatchers.Default) {
                    Timber.d("resizeImage: started ${width} ${height}")
                    if (originalBitmap.width <= width || width == 0) {
                        Timber.d("resizeImage: originalBitmap.width <= width || width == 0")
                    } else {
                        height = originalBitmap.height / (originalBitmap.width / width)
                        viewModelScope.launch {
                            withContext(Dispatchers.Default) {
                                Timber.d("resizeImage ${width} ${height}")
                                draftBitmap =
                                    Bitmap.createScaledBitmap(originalBitmap, width, height, false)
                                Timber.d("resizeImage finished ${draftBitmap.width} ${draftBitmap.height}")
                                imageProcessorManager.setBitmap(draftBitmap)
                                withContext(Dispatchers.Main) {
                                    _bitmap.value = draftBitmap
                                }
                            }
                        }


                    }
                }
            }
        }
        this.width = width
    }
}

