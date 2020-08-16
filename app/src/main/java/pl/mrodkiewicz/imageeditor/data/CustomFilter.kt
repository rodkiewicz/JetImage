package pl.mrodkiewicz.imageeditor.data


val defaultMatrix: FloatArray = floatArrayOf(
    1f, 0f, 0f, 0f, 0f, // red
    0f, 1f, 0f, 0f, 0f, // green
    0f, 0f, 1f, 0f, 0f, // blue
    0f, 0f, 0f, 1f, 0f  // alpha
)
class CustomFilter(override var matrix: FloatArray = defaultMatrix) : Filter {
    fun updateRed(value: Float): Filter {
        matrix[0] = value
        return this
    }

    fun updateBlue(value: Float): Filter {
        matrix[6] = value
        return this
    }

    fun updateGreen(value: Float): Filter {
        matrix[12] = value
        return this
    }

}