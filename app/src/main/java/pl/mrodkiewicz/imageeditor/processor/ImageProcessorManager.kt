package pl.mrodkiewicz.imageeditor.processor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.FilterMatrix
import timber.log.Timber
import java.util.*

class ImageProcessorManager(
    val convolutionMIP: ConvolutionMatrixImageProcessor,
    val colorFilterMIP: ColorFilterMatrixImageProcessor,
    @ApplicationContext val context: Context
) {
    // original NOT EDITABLE BITMAP
    private lateinit var originalBitmap: Bitmap

    // bitmap resized to device pixel dimensions
    private lateinit var draftBitmap: Bitmap

    private var cachedBitmap: Bitmap? = null
    private var cachedFilterID: UUID? = null
    var job: CompletableJob = Job()
    private var width = 0
    private var height = 0

    fun setBitmap(bitmap: Bitmap) {
        originalBitmap = bitmap
        draftBitmap = originalBitmap.copy(originalBitmap.config, true)
    }

    suspend fun process(filters: Pair<ImmutableList<Filter>, Filter>): Bitmap =
        withContext(Dispatchers.Default + job) {
            if (filters.second.id == cachedFilterID && cachedBitmap != null) {
                when (filters.second.filterMatrix) {
                    is FilterMatrix.ColorFilter -> colorFilterMIP.loadFilter(
                        cachedBitmap!!,
                        filters.second
                    )
                    else -> convolutionMIP.loadFilter(cachedBitmap!!, filters.second)
                }
            } else {
                cachedFilterID = UUID.randomUUID()
                process(filters.first)
            }
        }

    suspend fun process(filters: ImmutableList<Filter>): Bitmap =
        withContext(Dispatchers.Default + job) {
            var bitmap = draftBitmap.copy(draftBitmap.config, true)
            filters.forEach {
                when(it.filterMatrix){
                    is FilterMatrix.ColorFilter -> colorFilterMIP.loadFilter(
                        bitmap!!,
                        it
                    )
                    else -> convolutionMIP.loadFilter(bitmap!!, it)
                }
            }
            cache(bitmap!!, filters.last().id)
            return@withContext bitmap
        }

    fun cache(bitmap: Bitmap, filterId: UUID){
        cachedBitmap = bitmap.copy(bitmap.config,true)
        cachedFilterID = filterId
    }
    fun resizeImage() {
        if (originalBitmap.width >= width || originalBitmap.height >= height) {
            Timber.d("resizeImage: originalBitmap.width >= width || originalBitmap.height >= height")
            return
        }
        val scaleWidth = (width / originalBitmap.width).toFloat()
        val scaleHeight = (height / originalBitmap.height).toFloat()
        val matrix = Matrix()
        matrix.postScale(scaleWidth, scaleHeight)
        val resizedBitmap = Bitmap.createBitmap(
            originalBitmap, 0, 0, width, height, matrix, false
        )
        resizedBitmap?.let {
            draftBitmap = it
            Timber.d("resizeImage: bitmap resized")
        }
    }

    fun cleanup() {
        originalBitmap.recycle()
        draftBitmap.recycle()
        cachedBitmap?.recycle()

    }
}