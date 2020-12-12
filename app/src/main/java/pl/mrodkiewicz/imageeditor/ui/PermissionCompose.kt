package pl.mrodkiewicz.imageeditor.ui

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

/**
 * This is a modified version of:
 * https://gist.github.com/objcode/775fe45127fd40f17932f672ee203f72#file-permissions-kt-L78
 */


/**
 * Instantiate and manage it in composition like this
 */
@ExperimentalComposeApi
@ExperimentalCoroutinesApi
@Composable
fun checkSelfPermissionState(
    activity: AppCompatActivity,
    permission: String
): PermissionState {
    val key = currentComposer.currentCompoundKeyHash.toString()
    val call = remember(activity, permission) {
        PermissionResultCall(key, activity, permission)
    }
    // drive initialCheck and unregister from composition lifecycle
    onCommit(call) {
        call.initialCheck()
        onDispose {
            call.unregister()
        }
    }
    return call.checkSelfPermission()
}

class PermissionState(
    val permission: String,
    val hasPermission: Flow<Boolean>,
    val shouldShowRationale: Flow<Boolean>,
    private val launcher: ActivityResultLauncher<String>
) {
    fun launchPermissionRequest() = launcher.launch(permission)
}

@ExperimentalCoroutinesApi
private class PermissionResultCall(
    key: String,
    private val activity: AppCompatActivity,
    private val permission: String
) {

    // defer this to allow construction before onCreate
    private val hasPermission = MutableStateFlow<Boolean?>(null)
    private val showRationale = MutableStateFlow<Boolean?>(null)

    // Don't do this in onCreate because compose setContent may be called in Activity usage before
    // onCreate is dispatched to this lifecycle observer (as a result, need to manually unregister)
    private var call: ActivityResultLauncher<String> = activity.activityResultRegistry.register(
        "LocationPermissions#($key)",
        ActivityResultContracts.RequestPermission()
    ) { result ->
        onPermissionResult(result)
    }

    /**
     * Call this after [Activity.onCreate] to perform the initial permissions checks
     */
    fun initialCheck() {
        hasPermission.value = checkPermission()
        showRationale.value = checkShowRationale()
    }

    fun unregister() {
        call.unregister()
    }

    fun checkSelfPermission(): PermissionState {
        return PermissionState(
            permission,
            hasPermission.filterNotNull(),
            showRationale.filterNotNull(),
            call
        )
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun checkShowRationale(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.shouldShowRequestPermissionRationale(permission)
        } else {
            false
        }
    }

    private fun onPermissionResult(result: Boolean) {
        hasPermission.value = result
        showRationale.value = checkShowRationale()
    }
}

@SuppressLint("MissingPermission")
@ExperimentalCoroutinesApi
@Composable
fun NeedsPermission(
    permission: PermissionState,
    hasPermissionContent: @Composable() (() -> Unit),
    noPermissionContent: @Composable() (() -> Unit),
) {

    val hasLocationPermission = permission.hasPermission.collectAsState(false).value
    if (hasLocationPermission) {
        hasPermissionContent.invoke()
    } else {
        noPermissionContent.invoke()
    }

}

