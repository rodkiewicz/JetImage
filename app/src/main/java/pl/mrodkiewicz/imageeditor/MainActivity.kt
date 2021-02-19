package pl.mrodkiewicz.imageeditor

import android.Manifest
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.setContent
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
            getUriForCameraPhoto(applicationContext)?.let { mainViewModel.setBitmapUri(it) }
            splashScreenStateUI.value = splashScreenStateUI.value.copy(fileSelected = true)
        }
    }
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        Timber.d("getContent: ${uri.path}")
        mainViewModel.setBitmapUri(uri)
        splashScreenStateUI.value = splashScreenStateUI.value.copy(fileSelected = true)
    }


    @ExperimentalComposeApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageEditorTheme {
                MainScreen()
            }
        }

    }

    @ExperimentalComposeApi
    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        val fineLocationInActivity = checkSelfPermissionState(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        NavHost(navController, startDestination = "splashScreen") {
            composable("splashScreen") { SplashScreen(navController = navController, takePhoto, getContent, splashScreenStateUI) }
            composable("editorScreen") { EditorScreen(fineLocationInActivity,mainViewModel = mainViewModel) }

        }
    }


}


