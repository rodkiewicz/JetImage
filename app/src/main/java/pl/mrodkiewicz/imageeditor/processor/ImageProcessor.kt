package pl.mrodkiewicz.imageeditor.processor

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import pl.mrodkiewicz.imageeditor.R
import pl.mrodkiewicz.imageeditor.data.AdjustFilter
import pl.mrodkiewicz.imageeditor.data.FilterType
import pl.mrodkiewicz.imageeditor.data.LutFilter
import pl.mrodkiewicz.imageeditor.helpers.convertToLutFilter
import pl.mrodkiewicz.imageeditor.helpers.loadBitmap
import timber.log.Timber
import java.util.*

interface ImageProcessor {
    abstract val lutOutput: StateFlow<ImmutableList<LutFilter>>
    abstract val outputBitmap: StateFlow<Bitmap?>

    suspend fun setBitmapUri(uri: Uri) {}

    private suspend fun setBitmap(bitmap: Bitmap) {}

    suspend fun processAdjustFilter(filter: Pair<ImmutableList<AdjustFilter>, AdjustFilter>): Unit?

    suspend fun processLutFilter(lutFilter: LutFilter?) {}

    suspend fun save(): Uri

    fun cache(bitmap: Bitmap, filterId: UUID, filterIndex: Int) {}

    private suspend fun resizeImage() {}

    fun cleanup() {}

    suspend fun setWidth(width: Int) {}
}
