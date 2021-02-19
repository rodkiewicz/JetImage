package pl.mrodkiewicz.imageeditor.data

import android.graphics.Bitmap
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.toImmutableList


@Immutable
data class LutFilter(
    var name: String = "",
    var lutFilter: IntArray = intArrayOf(),
    var thumbnail: Bitmap? = null,
    var isActive: Boolean = false,
    var x: Int = 0,
    var y: Int = 0,
    var z: Int = 0,
)

val default_lut_filters = mutableListOf(
    LutFilter(
        name = "Red",
    ),
    LutFilter(
        name = "Red",
    ),
    LutFilter(
        name = "Red",
    ),
    LutFilter(
        name = "Red",
    ),
    LutFilter(
        name = "Red",
    ),

    ).toImmutableList()