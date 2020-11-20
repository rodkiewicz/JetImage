package pl.mrodkiewicz.imageeditor.helpers

import androidx.renderscript.Matrix3f


//if percentage is 0 the filter intensity is 0
public fun Matrix3f.serPercentageForMatrix(percentage: Int) {
    this.array[0] = this.array[0].getPercentageFromOne(percentage)
    this.array[1] = this.array[1].getPercentageFromZero(percentage)
    this.array[2] = this.array[2].getPercentageFromZero(percentage)
    this.array[3] = this.array[3].getPercentageFromZero(percentage)
    this.array[4] = this.array[4].getPercentageFromOne(percentage)
    this.array[5] = this.array[5].getPercentageFromZero(percentage)
    this.array[6] = this.array[6].getPercentageFromZero(percentage)
    this.array[7] = this.array[7].getPercentageFromZero(percentage)
    this.array[8] = this.array[8].getPercentageFromOne(percentage)
}
//if percentage is 0 the filter intensity is 0
public fun FloatArray.serPercentageForMatrix(percentage: Int): FloatArray {
    var floatArray = this
    floatArray[0] = floatArray[0].getPercentageFromOne(percentage)
    floatArray[1] = floatArray[1].getPercentageFromZero(percentage)
    floatArray[2] = floatArray[2].getPercentageFromZero(percentage)
    floatArray[3] = floatArray[3].getPercentageFromZero(percentage)
    floatArray[4] = floatArray[4].getPercentageFromOne(percentage)
    floatArray[5] = floatArray[5].getPercentageFromZero(percentage)
    floatArray[6] = floatArray[6].getPercentageFromZero(percentage)
    floatArray[7] = floatArray[7].getPercentageFromZero(percentage)
    floatArray[8] = floatArray[8].getPercentageFromOne(percentage)
    return floatArray
}
//if percentage is 0 the  float value is 1, if percentage is 100 the float value is float
public fun Float.getPercentageFromOne(percentage: Int): Float {
    return this+((1f-this)*((100f-percentage)/100f))
}
//if percentage is 0 the  float value is 0, if percentage is 100 the float value is float
public fun Float.getPercentageFromZero(percentage: Int): Float {
    return ( this / 100 * percentage)
}

