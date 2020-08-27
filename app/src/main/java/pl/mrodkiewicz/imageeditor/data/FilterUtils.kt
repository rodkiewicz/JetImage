package pl.mrodkiewicz.imageeditor.data

class FilterUtils(){
    fun convertToMatrixIndex(section: SECTION, updated_value: VALUE_UPDATED): Int {
        return ((5 * section.column) + updated_value.row)
    }
}