package ipca.example.spacefighter

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class HighScoreViewModel : ViewModel() {
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _highScore = MutableStateFlow(0)
    val highScore: StateFlow<Int> = _highScore

    fun setScore(newScore: Int) {
        _score.value = newScore
    }

    fun setHighScore(newHighScore: Int) {
        _highScore.value = newHighScore
    }

    fun loadHighScore(context: Context) {
        viewModelScope.launch {
            _highScore.value = readHighScore(context)
        }
    }

    fun updateHighScore(context: Context, newHighScore: Int) {
        viewModelScope.launch {
            saveHighScore(context, newHighScore)
            _highScore.value = newHighScore
        }
    }

    private fun readHighScore(context: Context): Int {
        return try {
            val fis = context.openFileInput("highscore.txt")
            val inputStreamReader = InputStreamReader(fis)
            val bufferedReader = BufferedReader(inputStreamReader)
            val line = bufferedReader.readLine()
            bufferedReader.close()
            inputStreamReader.close()
            fis.close()
            line.toIntOrNull() ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun saveHighScore(context: Context, score: Int) {
        try {
            val fos = context.openFileOutput("highscore.txt", Context.MODE_PRIVATE)
            fos.write(score.toString().toByteArray())
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}