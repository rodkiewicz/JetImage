package pl.mrodkiewicz.imageeditor.processor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.data.AdjustFilter
import pl.mrodkiewicz.imageeditor.data.FilterType
import pl.mrodkiewicz.imageeditor.data.LutFilter
import pl.mrodkiewicz.imageeditor.helpers.*
import timber.log.Timber
import java.util.*


class ImageProcessorManager(
    val convolutionMIP: ConvolutionMatrixImageProcessor,
    val colorFilterMIP: ColorFilterMatrixImageProcessor,
    val blurIP: BlurImageProcessor,
    val lutIP: LutImageProcessor,
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

    private val _lutOutput = MutableStateFlow<ImmutableList<Pair<Bitmap,LutFilter>>?>(null)
    val lutOutput: StateFlow<ImmutableList<Pair<Bitmap,LutFilter>>?> = _lutOutput



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
        getLutThumbnail()
        Timber.d("setBitmap ${bitmap.byteCount}")
    }

    suspend fun processAdjustFilter(filter: Pair<ImmutableList<AdjustFilter>, AdjustFilter>) =
        withContext(imageProcessorScope) {
            draftBitmap?.let { draftBitmap ->
                if (filter.second.id.equals(cachedFilterID) && cachedBitmap != null) {
                    cachedBitmap?.let {
                        var bitmap = it.copy(it.config, true)
                        bitmap?.let {
                            bitmap = processBitmap(bitmap, filter.second)
                        }
                        filter.first.subList(cacheIndex + 1, filter.first.lastIndex + 1).forEach {
                            if (it.value > 0) {
                                bitmap = processBitmap(bitmap, it)
                            }
                        }
                        _outputBitmap.value = bitmap
                        cache(it, filter.second.id, cacheIndex)
                    }
                    return@withContext
                } else {
                    var bitmap = draftBitmap.copy(draftBitmap.config, true)
                    bitmap?.let {
                        filter.first.forEachIndexed { index, it ->
                            if (it.id.equals(filter.second.id)) {
                                cache(bitmap, filter.second.id, index)
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

    suspend fun processLutFilter(lutFilter: LutFilter?) {
        withContext(imageProcessorScope) {
            draftBitmap?.let { draftBitmap ->
                lutFilter?.let { lutFilter ->
                    var bitmap = draftBitmap.copy(draftBitmap.config, true)
                    bitmap?.let {
                        bitmap = processBitmap(bitmap, lutFilter)
                        lutFilter?.let { it1 ->Timber.d("processLutFilter ${lutFilter}")}
                        _outputBitmap.value = bitmap
                    }
                } ?: run{
                    var bitmap = draftBitmap.copy(draftBitmap.config, true)
                    _outputBitmap.value = bitmap
                }
            }
        }
    }

    private fun getLutFilters(): List<LutFilter>{
        var listOfDrawables = listOf(
            R.drawable.lut_bleach,
            R.drawable.lut_blue_crush,
            R.drawable.lut_bw_contrast,
            R.drawable.lut_instant,
            R.drawable.lut_punch,
            R.drawable.lut_vintage,
            R.drawable.lut_washout,
            R.drawable.lut_washout_color,
            R.drawable.lut_x_process
        ).map {
            BitmapFactory.decodeResource(context.resources, it).convertToLutFilter("essa")
        }
        return listOfDrawables.toImmutableList()
    }

    private fun getLutThumbnail(){
        var lutFilters = getLutFilters()
        var list = mutableListOf<Pair<Bitmap, LutFilter>>()
        draftBitmap?.let {
            var thumbnailBitmap = Bitmap.createScaledBitmap(
                it,
                100,
                it.height * 100 / it.width,
                false
            )
            lutFilters.forEach {
                list.add(Pair(processBitmap(thumbnailBitmap, it), it))
            }
        }
        _lutOutput.value = list.toImmutableList()
    }

    suspend fun save(adjustFilters: ImmutableList<AdjustFilter>): Uri =
        withContext(Dispatchers.Default) {
            bitmapUri.loadBitmap(context).let { bmp ->
                val bitmaps = bmp.divideIntoTiles(4)
                val outputBitmaps = mutableListOf<Bitmap>()
                bitmaps.forEach { bitmap ->
                    var outputBitmap = bitmap.copy(bitmap.config, true)
                    adjustFilters.forEach {
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
                    }.toTypedArray(), output.absolutePath, 4)
                    Timber.d("to trwalo ${(System.currentTimeMillis() - startTime) / 1000}")
                    addImageToGallery(context, output.absolutePath)
                }
                FileProvider.getUriForFile(
                    context,
                    "pl.mrodkiewicz.imageeditor.provider",
                    output
                )
            }
        }

    private fun processBitmap(bitmap: Bitmap, filter: AdjustFilter): Bitmap {
        return when (filter.filterMatrix) {
            is FilterType.ColorFilter -> colorFilterMIP.loadFilter(bitmap, filter)
            is FilterType.Blur -> blurIP.loadBlur(bitmap, filter)
            is FilterType.Convolve5x5, is FilterType.Convolve3x3 -> convolutionMIP.loadFilter(
                bitmap,
                filter
            )
        }
    }

    private fun processBitmap(bitmap: Bitmap, filter: LutFilter): Bitmap {
        return lutIP.loadFilter(bitmap, filter)
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




