package pl.mrodkiewicz.imageeditor

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import java.text.SimpleDateFormat

fun Map<String, Boolean>.checkIfGranted(): Boolean {
    return !(this.map { it.value }.contains(false))
}

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this.requireContext(), message, duration).apply { show() }
}

fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT): Toast {
    return Toast.makeText(this, message, duration).apply { show() }
}

fun Activity.snackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar {
    return Snackbar.make(this.findViewById<ViewGroup>(android.R.id.content), message, duration)
}
fun Fragment.snackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT): Snackbar {
    return Snackbar.make(this.requireView(), message, duration)
}

fun SeekBar.afterValueChangedFlow(): Flow<Int> {
    return callbackFlow {
        val watcher = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                offer(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // NO-OP
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // NO-OP
            }

        }

        setOnSeekBarChangeListener(watcher)
        awaitClose { setOnSeekBarChangeListener(null) }
    }
}

