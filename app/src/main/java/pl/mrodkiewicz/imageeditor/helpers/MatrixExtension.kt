package pl.mrodkiewicz.imageeditor.helpers

import androidx.renderscript.Matrix3f
import timber.log.Timber


//if percentage is 0 the filter intensity is 0
fun Matrix3f.serPercentageForMatrix(percentage: Int) {
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

//if percentage is 0 the  float value is 1, if percentage is 100 the float value is float
fun Float.getPercentageFromOne(percentage: Int): Float {
    return this+((1f-this)*((100f-percentage)/100f))
}
//if percentage is 0 the  float value is 0, if percentage is 100 the float value is float
fun Float.getPercentageFromZero(percentage: Int): Float {
    return ( this / 100 * percentage)
}