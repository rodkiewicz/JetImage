package pl.mrodkiewicz.imageeditor.processor

import android.content.Context
import android.graphics.Bitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.FilterMatrix
import timber.log.Timber
import java.util.*

// TODO BUILD CACHE PIPELINE
class ImageProcessorManager(
    val convolutionMIP: ConvolutionMatrixImageProcessor,
    val colorFilterMIP: ColorFilterMatrixImageProcessor,
    val blurIP: BlurImageProcessor,
    @ApplicationContext val context: Context
) {
    private lateinit var originalBitmap: Bitmap

    // bitmap resized to device pixel dimensions
    private lateinit var draftBitmap: Bitmap
    private var width = 0

    private var cachedBitmap: Bitmap? = null
    private var cachedFilterID: UUID? = null
    private var cacheIndex = -1


    private val handler = CoroutineExceptionHandler { _, exception ->
        Timber.e("ImageProcessorManager got $exception")
    }
    private var job: CompletableJob = Job()
    private var imageProcessorScope = Dispatchers.Default + handler
    private val _outputBitmap = MutableStateFlow<Bitmap?>(null)
    val outputBitmap: StateFlow<Bitmap?> = _outputBitmap

    suspend fun setBitmap(bitmap: Bitmap) {
        originalBitmap = bitmap
        draftBitmap = originalBitmap.copy(originalBitmap.config, true)
        setWidth(width)
        Timber.d("setBitmap ${bitmap.byteCount}")
    }

    suspend fun process(filters: Pair<ImmutableList<Filter>, Filter>) =
        withContext(imageProcessorScope) {
            if (!::draftBitmap.isInitialized)
                return@withContext
            if (filters.second.id.equals(cachedFilterID) && cachedBitmap != null) {
                cachedBitmap?.let {
                    var bitmap = it.copy(it.config, true)
                    bitmap?.let {
                        bitmap = processBitmap(bitmap, filters.second)
                    }

                    filters.first.subList(cacheIndex+1,filters.first.lastIndex+1).forEach {
                        if (it.value > 0) {
                            bitmap = processBitmap(bitmap, it)
                        }
                    }
                    _outputBitmap.value = bitmap
                    cache(it, filters.second.id, cacheIndex)
                }
                return@withContext
            } else {
                var bitmap = draftBitmap.copy(draftBitmap.config, true)
                bitmap?.let {
                    filters.first.forEachIndexed{ index, it ->
                        if(it.id.equals(filters.second.id)){
                            cache(bitmap, filters.second.id, index)
                        }
                        if (it.value > 0) {
                            bitmap = processBitmap(bitmap, it)
                        }

                    }
                    _outputBitmap.value = bitmap
                }
            }
        }


    private fun processBitmap(bitmap: Bitmap, filters: Filter): Bitmap {
        Timber.d("process $filters")
        return when (filters.filterMatrix) {
            is FilterMatrix.ColorFilter -> colorFilterMIP.loadFilter(bitmap, filters)
            is FilterMatrix.Blur -> blurIP.loadBlur(bitmap, filters)
            is FilterMatrix.Convolve5x5, is FilterMatrix.Convolve3x3 -> convolutionMIP.loadFilter(
                bitmap,
                filters
            )
        }
    }

    fun cache(bitmap: Bitmap, filterId: UUID, filterIndex: Int) {
        cachedBitmap = bitmap.copy(bitmap.config, true)
        cachedFilterID = filterId
        cacheIndex = filterIndex
        Timber.d("cache ${bitmap.hashCode()} ${filterId}")
    }

    suspend fun resizeImage() {
        withContext(imageProcessorScope) {
            draftBitmap =
                Bitmap.createScaledBitmap(
                    originalBitmap,
                    width,
                    originalBitmap.height * width / originalBitmap.width,
                    false
                )
            Timber.d("resizeImage finished ${draftBitmap.width} ${draftBitmap.height}")
            _outputBitmap.value = draftBitmap
        }
    }

    fun cleanup() {
        originalBitmap.recycle()
        draftBitmap.recycle()
//        cachedBitmap?.recycle()
    }

    suspend fun setWidth(width: Int) {
        if (width != 0) {
            this.width = width
        }
        if (!::originalBitmap.isInitialized || !::draftBitmap.isInitialized) {
            return
        }
        if ((this.width != 0) && (draftBitmap.width != this.width)) {
            if (originalBitmap.width <= width || width == 0) {
                Timber.d("resizeImage: originalBitmap.width <= width || width == 0")
            } else {
                withContext(Dispatchers.Default) {
                    resizeImage()
                }
            }

        }

}