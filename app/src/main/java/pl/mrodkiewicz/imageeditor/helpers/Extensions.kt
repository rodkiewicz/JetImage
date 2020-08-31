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
fun Int.toHSVValue(): Int {
    return this
}


fun Int.toHSVSaturation(): Int {
    return this
}


fun Int.fromHSVValue(): Int {
    return this
}

fun Int.fromHSVSaturation(): Int {
    return this
}

fun Int.toHSVHue(): Int {
    return this
}

fun Int.fromHSVHue(): Int {
    return this
}

// RGB
fun Int.toRGB(): Int {
    return this
}

fun Int.fromRGB(): Int {
    return this
}


