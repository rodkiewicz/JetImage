package pl.mrodkiewicz.imageeditor.data


data class Filter(
    var red: Int = 0,
    var green: Int = 0,
    var blue: Int = 0,
    var hue: Int = 0,
    var saturation: Int = 0,
    var value: Int = 0
)
