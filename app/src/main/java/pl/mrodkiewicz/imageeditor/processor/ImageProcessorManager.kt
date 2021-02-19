package pl.mrodkiewicz.imageeditor.processor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.data.*
import pl.mrodkiewicz.imageeditor.helpers.*
import timber.log.Timber
import java.util.*


class ImageProcessorManager(
    val convolutionMIP: ConvolutionMatrixImageProcessor,
    val colorFilterMIP: ColorFilterMatrixImageProcessor,
    val blurIP: BlurImageProcessor,
    val lutIP: LutImageProcessor,
    @ApplicationContext val context: Context
): ImageProcessor {
    private lateinit var bitmapUri: Uri

    private var originalBitmap: Bitmap? = null
    private var draftBitmap: Bitmap? = null
    private var width = 0

    var cache = Cache()

    private val handler = CoroutineExceptionHandler { _, exception ->
        Timber.e("ImageProcessorManager got $exception")
    }

    private var imageProcessorScope = Dispatchers.Default + handler
    private val _outputBitmap = MutableStateFlow<Bitmap?>(null)
    override val outputBitmap: StateFlow<Bitmap?> = _outputBitmap


    private val _lutOutput = MutableStateFlow(default_lut_filters)
    val lutOutput: StateFlow<ImmutableList<LutFilter>> = _lutOutput


    override suspend fun setBitmapUri(uri: Uri) {
        withContext(Dispatchers.IO) {
            draftBitmap = null
            originalBitmap = null
            _outputBitmap.value = null
            bitmapUri = uri
            setBitmap(uri.loadBitmap(context))
        }
    }


    private suspend fun setBitmap(bitmap: Bitmap) {
        originalBitmap = bitmap.copy(bitmap.config, true)
        setWidth(width)
        getLutThumbnails()
    }

    override suspend fun processAdjustFilter(filter: Pair<ImmutableList<AdjustFilter>, AdjustFilter>) =
        withContext(imageProcessorScope) {
            draftBitmap?.let { draftBitmap ->
                if (filter.second.id == cache.filterID && cache.bitmap != null) {
                    cache.bitmap?.let { it ->
                        var bitmap = it.copy(it.config, true)
                        bitmap?.let {
                            bitmap = processBitmap(bitmap, filter.second)
                        }
                        filter.first.subList(cache.index + 1, filter.first.lastIndex + 1).forEach {
                            if (it.value > 0) {
                                bitmap = processBitmap(bitmap, it)
                            }
                        }
                        _outputBitmap.value = bitmap
                        cache(it, filter.second.id, cache.index)
                    }
                    return@withContext
                } else {
                    var bitmap = draftBitmap.copy(draftBitmap.config, true)
                    bitmap?.let {
                        filter.first.forEachIndexed { index, it ->
                            if (it.id == filter.second.id) {
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

    override suspend fun processLutFilter(lutFilter: LutFilter?) {
        withContext(imageProcessorScope) {
            lutFilter?.let { lutFilter ->
                draftBitmap = originalBitmap?.let { processBitmap(it, lutFilter) }
                _outputBitmap.value = draftBitmap
            } ?: run {
                val bitmap = originalBitmap?.copy(originalBitmap?.config, true)
                _outputBitmap.value = bitmap
            }
        }
    }


    private fun getLutFilters(): List<LutFilter> {
        val listOfDrawables = listOf(
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

    private fun getLutThumbnails() {
        val lutFilters = getLutFilters()
        val list = mutableListOf<LutFilter>()
        originalBitmap?.let {
            val thumbnailBitmap = Bitmap.createScaledBitmap(
                it,
                100,
                it.height * 100 / it.width,
                false
            )
            lutFilters.forEach {
                it.thumbnail = processBitmap(thumbnailBitmap, it)
                list.add(it)
            }
        }
        _lutOutput.value = list.toImmutableList()
    }

    // TODO: SAVING WITH FULL RESOLUTION USING RENDERSCRIPT
    override suspend fun save(): Uri =
        withContext(Dispatchers.Default) {
            return@withContext draftBitmap?.saveImageAndAddToGallery(context)!!
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


    private fun cache(bitmap: Bitmap, filterId: UUID, filterIndex: Int) {
        cache = Cache(
                bitmap.copy(bitmap.config, true),
                filterId,
                filterIndex
        )


    }

    private suspend fun resizeImage() {
        withContext(imageProcessorScope) {
            originalBitmap =
                originalBitmap?.let {
                    Bitmap.createScaledBitmap(
                        it,
                        width,
                        it.height * width / it.width,
                        false
                    )
                }
            _outputBitmap.value = originalBitmap
            draftBitmap = originalBitmap?.copy(originalBitmap?.config, true)
        }
    }


    fun cleanup() {
        draftBitmap = null
        _outputBitmap.value = null
        System.gc()
    }

    override suspend fun setWidth(width: Int) {
        if (width != 0) {
            this.width = width
        }
        originalBitmap?.let { originalBitmap ->
            if ((this.width != 0) && (originalBitmap.width != this.width)) {
                if ((originalBitmap.width <= width || width == 0) && _outputBitmap.value == null) {
                    draftBitmap = originalBitmap.copy(originalBitmap.config, true)
                    _outputBitmap.value = draftBitmap
                } else {
                    withContext(Dispatchers.Default) {
                        resizeImage()
                    }
                }

            }
        }
    }
}




