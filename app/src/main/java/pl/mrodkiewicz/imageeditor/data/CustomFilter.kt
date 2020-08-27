package pl.mrodkiewicz.imageeditor.data


val defaultMatrix: FloatArray = floatArrayOf(
    1f, 0f, 0f, 0f, 0f, // red
    0f, 1f, 0f, 0f, 0f, // green
    0f, 0f, 1f, 0f, 0f, // blue
    0f, 0f, 0f, 1f, 0f  // alpha
)
class CustomFilter(override var matrix: FloatArray = defaultMatrix) : Filter {
    fun update(index: Int,value: Float): Filter {
        matrix[index] = value
        return this
    }

}