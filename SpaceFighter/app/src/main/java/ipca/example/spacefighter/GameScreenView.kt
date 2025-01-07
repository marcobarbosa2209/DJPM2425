package ipca.example.spacefighter

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun GameScreenView(
    onGameOver: (finalScore: Int) -> Unit = {}
) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    val screenHeight = configuration.screenHeightDp

    val density = configuration.densityDpi / 160f
    val screenWidthPx = screenWidth * density
    val screenHeightPx = screenHeight * density

    val lifecycleOwner = LocalLifecycleOwner.current

    AndroidView(factory = { context ->
        GameView(context, screenWidthPx.toInt(), screenHeightPx.toInt())
    }, update = { gameView ->
        gameView.onGameOver = { finalScore ->
            onGameOver(finalScore)
        }
        gameView.resume()
    })
}