package pl.mrodkiewicz.imageeditor.editor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.applyFilter
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class EditorViewModel : ViewModel() {

    private val _bitmap = MutableLiveData<Bitmap>()
    private val _filter = MutableLiveData<Filter>(Filter())
    private val _progress = MutableLiveData<Int>(0)
    private lateinit var progressChannel: Channel<Int>
    private lateinit var filterChannel: Channel<Filter>
    val bitmap: LiveData<Bitmap> = _bitmap
    val progress: LiveData<Int> = _progress
    val filter: LiveData<Filter> = _filter
    private var originalBitmap: Bitmap
    private lateinit var bitmapJob: Deferred<Bitmap>

    init {
        originalBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        _filter.value = Filter()
        progressChannel = Channel<Int>()
        viewModelScope.launch {
            progressChannel.consumeAsFlow().collect {
                _progress.value = it
                Timber.d("progress: ${it}")
            }
        }
        filterChannel = Channel<Filter>()
        viewModelScope.launch {
            filterChannel.consumeAsFlow().distinctUntilChanged().collect {
                withContext(Dispatchers.Default) {
                    resetJob()
                    bitmapJob = async {
                        Timber.d("filter: ${it}")
                        originalBitmap.applyFilter(it, progressChannel)
                    }
                    val newBitamp = bitmapJob.await()
                    withContext(Dispatchers.Main) {
                        _bitmap.value = newBitamp
                    }
                }
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


    fun updateFilter(newFilter: Filter) {
        _filter.value = newFilter
        filterChannel.offer(newFilter)
    }

    fun resetFilter() {
        _filter.value = Filter(red = 255, green = 255, blue = 255)
    }

    fun resetJob() {
        if (::bitmapJob.isInitialized) {
            bitmapJob.cancel("Reset job")
            Timber.d("resetJob")
        }
    }


    override fun onCleared() {
        super.onCleared()
        originalBitmap.recycle()
        bitmap.value?.recycle()
    }

}



