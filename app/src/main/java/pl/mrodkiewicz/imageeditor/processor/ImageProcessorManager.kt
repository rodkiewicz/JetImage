package pl.mrodkiewicz.imageeditor.processor

import android.R.attr.path
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.*
import pl.mrodkiewicz.imageeditor.data.Filter
import pl.mrodkiewicz.imageeditor.data.FilterMatrix
import timber.log.Timber
import java.util.*


class ImageProcessorManager(
    val convolutionMIP: ConvolutionMatrixImageProcessor,
    val colorFilterMIP: ColorFilterMatrixImageProcessor,
    @ApplicationContext val context: Context
) {
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
//            if (filters.second.id == cachedFilterID && cachedBitmap != null) {
//                Timber.d("process with cache")
//                return@withContext when (filters.second.filterMatrix) {
//                    is FilterMatrix.ColorFilter -> {
//                        var bitmap = colorFilterMIP.loadFilter(
//                            cachedBitmap!!,
//                            filters.second
//                        )
//                        cache(bitmap, filters.second.id)
//                        bitmap
//                    }
//                    else -> {
//                        var bitmap = convolutionMIP.loadFilter(cachedBitmap!!, filters.second)
//                        cache(bitmap, filters.second.id)
//                        bitmap
//                    }
//
//                }
//            } else {
            return@withContext process(filters.first)
//            }
        }

    suspend fun process(filters: ImmutableList<Filter>): Bitmap =
        withContext(Dispatchers.Default + job) {
            var bitmap = draftBitmap.copy(draftBitmap.config, true)
            Timber.d("filters =====")
            filters.forEach {
                Timber.d("filters ${it.name}")
                when (it.filterMatrix) {
                    is FilterMatrix.ColorFilter -> bitmap = colorFilterMIP.loadFilter(
                        bitmap!!,
                        it
                    )
                    else -> bitmap = convolutionMIP.loadFilter(bitmap!!, it)
                }
            }
            Timber.d("bitmap hashcodes ${draftBitmap.hashCode()} ${bitmap.hashCode()}")
            Timber.d(
                "bitmap value r ${
                    Color(
                        bitmap!!.getPixel(
                            100,
                            100
                        )
                    ).red
                } g ${Color(bitmap!!.getPixel(100, 100)).green} b ${
                    Color(
                        bitmap!!.getPixel(
                            100,
                            100
                        )
                    ).blue
                }"
            )

            return@withContext bitmap
        }

    fun cache(bitmap: Bitmap, filterId: UUID) {
        cachedBitmap = bitmap.copy(bitmap.config, true)
        cachedFilterID = filterId
        Timber.d("cache ${bitmap.hashCode()} ${filterId}")
    }

    fun resizeImage() {
        Timber.d("resizeImage ${width} ${height}")
            Glide
                .with(context)
                .load(originalBitmap)
                .apply(RequestOptions().override(width, height))
                .into(object : SimpleTarget<Drawable>(){
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        draftBitmap = resource.toBitmap()
                    }

                })
    }

    fun cleanup() {
        originalBitmap.recycle()
        draftBitmap.recycle()
        cachedBitmap?.recycle()

    }

    suspend fun setWidth(width: Int) {

    }
}