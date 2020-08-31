package pl.mrodkiewicz.imageeditor

import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.roundToInt



fun Fragment.snackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar {
    return Snackbar.make(this.requireView(), message, duration)
}

fun Slider.afterValueChangedFlow(): Flow<Float> {
    return callbackFlow {
        val listener =
            Slider.OnChangeListener { slider, value, fromUser -> offer(value) }
        addOnChangeListener(listener)
        awaitClose { removeOnChangeListener(listener) }
    }
}


// HSV
fun Float.toHSVValue(): Float {
    return this
}


fun Float.toHSVSaturation(): Float {
    return this
}


fun Float.fromHSVValue(): Int {
    return this.roundToInt()
}

fun Float.fromHSVSaturation(): Int {
    return this.roundToInt()
}

fun Float.toHSVHue(): Float {
    return this / 359 * 100
}

fun Float.fromHSVHue(): Int {
    return (this / 100 * 359).roundToInt()
}

// RGB
fun Float.toRGB(): Float {
    return this / 100 * 255
}

fun Float.fromRGB(): Int {
    return (this / 255 * 100).roundToInt()
}


