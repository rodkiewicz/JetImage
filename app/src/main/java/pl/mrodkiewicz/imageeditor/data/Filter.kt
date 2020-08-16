package pl.mrodkiewicz.imageeditor.data


interface Filter{
    var matrix : FloatArray
}
enum class VALUE_UPDATED {
    RED, GREEN, BLUE, HUE
}