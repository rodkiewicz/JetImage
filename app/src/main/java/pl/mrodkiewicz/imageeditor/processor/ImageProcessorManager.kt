package pl.mrodkiewicz.imageeditor.processor

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.FilterMatrix
import pl.mrodkiewicz.imageeditor.helpers.*
import timber.log.Timber
import java.util.*


class ImageProcessorManager(
    val convolutionMIP: ConvolutionMatrixImageProcessor,
    val colorFilterMIP: ColorFilterMatrixImageProcessor,
    val blurIP: BlurImageProcessor,
    @ApplicationContext val context: Context
) {
    private lateinit var bitmapUri: Uri

    // bitmap resized to device pixel dimensions
    private var draftBitmap: Bitmap? = null
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

    suspend fun setBitmapUri(uri: Uri) {
        withContext(Dispatchers.IO) {
            draftBitmap = null
            _outputBitmap.value = null
            bitmapUri = uri
            setBitmap(uri.loadBitmap(context))
        }
    }


    private suspend fun setBitmap(bitmap: Bitmap) {
        draftBitmap = bitmap.copy(bitmap.config, true)
        setWidth(width)
        Timber.d("setBitmap ${bitmap.byteCount}")
    }

    suspend fun process(filters: Pair<ImmutableList<Filter>, Filter>) =
        withContext(imageProcessorScope) {
            draftBitmap?.let { draftBitmap ->
                if (filters.second.id.equals(cachedFilterID) && cachedBitmap != null) {
                    cachedBitmap?.let {
                        var bitmap = it.copy(it.config, true)
                        bitmap?.let {
                            bitmap = processBitmap(bitmap, filters.second)
                        }
                        filters.first.subList(cacheIndex + 1, filters.first.lastIndex + 1).forEach {
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
                        filters.first.forEachIndexed { index, it ->
                            if (it.id.equals(filters.second.id)) {
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
        }

    suspend fun save(filters: ImmutableList<Filter>): Uri =
        withContext(Dispatchers.Default) {
            bitmapUri.loadBitmap(context).let { bmp ->
                val bitmaps = bmp.divideIntoTiles(4)
                val outputBitmaps = mutableListOf<Bitmap>()
                bitmaps.forEach { bitmap ->
                    var outputBitmap = bitmap.copy(bitmap.config, true)
                    filters.forEach {
                        if (it.value > 0) {
                            outputBitmap = processBitmap(outputBitmap, it)
                        }
                    }
                    outputBitmap.let { it1 -> outputBitmaps.add(it1) }
                    bitmap.recycle()
                }
                var output = createPictureUri(context, "output", "output")
                withContext(Dispatchers.IO) {
                    var startTime = System.currentTimeMillis()
                    SampleTileImage.doTiling(outputBitmaps.mapIndexed { index, it ->
                        it.saveImage(
                            context,
                            "jetimage",
                            "jetimage00000${index}"
                        )
                    }.toTypedArray(),output.absolutePath,4)
                    Timber.d("to trwalo ${(System.currentTimeMillis() - startTime)/1000}")
                    addImageToGallery(context,output.absolutePath)
                }
                FileProvider.getUriForFile(
                    context,
                    "pl.mrodkiewicz.imageeditor.provider",
                    output
                )
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

    private suspend fun resizeImage() {
        Timber.d("resizeImage started ${width} ${draftBitmap?.byteCount}")

        withContext(imageProcessorScope) {
            draftBitmap =
                draftBitmap?.let {
                    Bitmap.createScaledBitmap(
                        it,
                        width,
                        it.height * width / it.width,
                        false
                    )
                }
            Timber.d("resizeImage finished ${draftBitmap?.width} ${draftBitmap?.height}")
            _outputBitmap.value = draftBitmap
        }
    }

    fun cleanup() {
        draftBitmap?.recycle()
        cachedBitmap?.recycle()
        draftBitmap = null
        _outputBitmap.value = null
    }

    suspend fun setWidth(width: Int) {
        if (width != 0) {
            this.width = width
        }
        draftBitmap?.let { draftBitmap ->
            if ((this.width != 0) && (draftBitmap.width != this.width)) {
                if (draftBitmap.width <= width || width == 0) {
                    Timber.d("resizeImage: originalBitmap.width <= width || width == 0")
                } else {
                    withContext(Dispatchers.Default) {
                        resizeImage()
                    }
                }

            }
        }
    }
}




