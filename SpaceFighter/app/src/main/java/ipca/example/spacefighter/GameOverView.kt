package ipca.example.spacefighter

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameOverView(
    modifier: Modifier = Modifier,
    onResumeClick: () -> Unit = {},
    finalScore: Int = 0,
    highScore: Int = 0
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Game Over",
                fontSize = 50.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Score: $finalScore",
                fontSize = 30.sp,
                color = Color.Red
            )
            Text(
                text = "High Score: $highScore",
                fontSize = 30.sp,
                color = Color.Blue
            )
            Spacer(modifier = Modifier.height(32.dp))
            Image(
                painter = painterResource(id = R.drawable.playnow),
                contentDescription = "Play Again",
                modifier = Modifier
                    .width(200.dp)
                    .height(80.dp)
                    .clickable { onResumeClick() },
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GameOverViewPreview() {
    GameOverView(finalScore = 150, highScore = 200)
}