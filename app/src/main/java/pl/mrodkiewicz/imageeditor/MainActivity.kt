package pl.mrodkiewicz.imageeditor

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.mrodkiewicz.imageeditor.ui.EditorScreen
import pl.mrodkiewicz.imageeditor.ui.ImageEditorTheme
import pl.mrodkiewicz.imageeditor.ui.SplashScreen


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//m
        setContent {
            ImageEditorTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()

        NavHost(navController, startDestination = "splashScreen") {
            composable("splashScreen") { SplashScreen(navController = navController) }
            composable("editorScreen") { EditorScreen(mainViewModel = mainViewModel) }

        }
    }
}


