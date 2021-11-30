package pl.mrodkiewicz.imageeditor

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.mrodkiewicz.imageeditor.helpers.getUriForCameraPhoto
import pl.mrodkiewicz.imageeditor.ui.editorscreen.EditorScreen
import pl.mrodkiewicz.imageeditor.ui.ImageEditorTheme
import pl.mrodkiewicz.imageeditor.ui.editorscreen.checkSelfPermissionState
import pl.mrodkiewicz.imageeditor.ui.splashscreen.SplashScreen
import pl.mrodkiewicz.imageeditor.ui.splashscreen.SplashScreenStateUI
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private var splashScreenStateUI = mutableStateOf(SplashScreenStateUI(false))
    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()){ isSaved ->
        if(isSaved){
            getUriForCameraPhoto(applicationContext)?.let { uri = it.toString() }
            splashScreenStateUI.value = splashScreenStateUI.value.copy(fileSelected = true)
        }
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){
        it?.let {
            Timber.d("getContent: ${it.path}")
            uri = it.toString()
            splashScreenStateUI.value = splashScreenStateUI.value.copy(fileSelected = true)
        }
    }
    private var uri = ""

    @ExperimentalComposeApi
    @OptIn(InternalComposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageEditorTheme {
                MainScreen()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if(uri != ""){
            mainViewModel.setBitmapUri(Uri.parse(uri))
            uri = ""
        }
    }

    @InternalComposeApi
    @ExperimentalComposeApi
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val fineLocationInActivity = checkSelfPermissionState(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        NavHost(navController, startDestination = "splashScreen") {
            composable("splashScreen") { SplashScreen(navController = navController, takePhoto, getContent, splashScreenStateUI.value) }
            composable("editorScreen") { EditorScreen(fineLocationInActivity,mainViewModel = mainViewModel) }

        }
    }


}


