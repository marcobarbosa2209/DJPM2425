package ipca.example.spacefighter

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ipca.example.spacefighter.ui.theme.SpaceFighterTheme

class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val highScoreViewModel: HighScoreViewModel = viewModel()

            highScoreViewModel.loadHighScore(this)

            SpaceFighterTheme {
                Scaffold(modifier = androidx.compose.ui.Modifier.fillMaxSize()) { _ ->
                    NavHost(navController = navController,
                        startDestination = "game_start") {
                        composable("game_start") {
                            val currentHighScore by highScoreViewModel.highScore.collectAsState()
                            GameHomeView(
                                onPlayClick = {
                                    navController.navigate("game_screen")
                                },
                                highScore = currentHighScore
                            )
                        }
                        composable("game_screen") {
                            GameScreenView() { finalScore ->
                                // Update ViewModel with final score
                                highScoreViewModel.setScore(finalScore)
                                // Update high score if necessary
                                if (finalScore > highScoreViewModel.highScore.value) {
                                    // Use Activity context to pass to ViewModel
                                    highScoreViewModel.updateHighScore(this@MainActivity, finalScore)
                                }
                                navController.navigate("game_over")
                            }
                        }
                        composable("game_over") {
                            val finalScore by highScoreViewModel.score.collectAsState()
                            val currentHighScore by highScoreViewModel.highScore.collectAsState()
                            GameOverView(
                                finalScore = finalScore,
                                highScore = currentHighScore,
                                onResumeClick = {
                                    navController.popBackStack("game_start", inclusive = false)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}