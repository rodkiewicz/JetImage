package pl.mrodkiewicz.imageeditor.data


interface Filter{
    var matrix : FloatArray
}
enum class VALUE_UPDATED(var row: Int) {
    RED(0), GREEN(1), BLUE(2)
}
enum class SECTION(var column: Int) {
    RED(0),  GREEN(1), BLUE(2), ALPHA(3);
}