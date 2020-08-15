package pl.mrodkiewicz.imageeditor.data

var DEFAULT_VALUE = -1

data class Filter(var red: Int = DEFAULT_VALUE, var green: Int = DEFAULT_VALUE, var blue: Int = DEFAULT_VALUE, var hue: Int = DEFAULT_VALUE)
enum class VALUE_UPDATED {
    RED, GREEN, BLUE, HUE
}