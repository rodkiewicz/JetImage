package pl.mrodkiewicz.imageeditor.data

import android.graphics.Bitmap
import java.util.*

data class Cache(
        val bitmap: Bitmap? = null,
        val filterID: UUID? = null,
        val index : Int = -1
)

