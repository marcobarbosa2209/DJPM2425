package ipca.example.spacefighter

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import java.util.Random

class GameView(context: Context, width: Int, height: Int) : SurfaceView(context), Runnable {

    private var playing = false
    private var gameThread: Thread? = null
    private val surfaceHolder: SurfaceHolder = holder
    private var canvas: Canvas? = null

    private val paint: Paint = Paint()
    private val stars = arrayListOf<Star>()
    private val enemies = arrayListOf<Enemy>()
    private lateinit var player: Player
    private lateinit var boom: Boom
    private lateinit var warrior: Warrior

    private var lives = 3
    var score = 0
    private var highScore = 0

    private val heartSize = 50
    private val heartBitmap: Bitmap

    var onGameOver: (finalScore: Int) -> Unit = {}

    private var callGameOverOnce = false

    private val generator = Random()

    private var activePointers = 0
    private val touchX = FloatArray(10) { -1f }
    private val touchY = FloatArray(10) { -1f }

    private val gameContext: Context = context

    init {
        heartBitmap = getHeartBitmap()
        init(context, width, height)
    }

    private fun getHeartBitmap(): Bitmap {
        val drawable = ContextCompat.getDrawable(gameContext, R.drawable.heart)
        return if (drawable != null) {
            val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
            DrawableCompat.setTint(wrappedDrawable, Color.RED)
            val bitmap = Bitmap.createBitmap(
                heartSize,
                heartSize,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            wrappedDrawable.setBounds(0, 0, heartSize, heartSize)
            wrappedDrawable.draw(canvas)
            bitmap
        } else {
            Bitmap.createBitmap(heartSize, heartSize, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.RED)
            }
        }
    }

    private fun init(context: Context, width: Int, height: Int) {
        for (i in 0..100) {
            stars.add(Star(width, height))
        }

        for (i in 0..2) {
            enemies.add(Enemy(context, width, height))
        }

        player = Player(context, width, height)
        warrior = Warrior(context, width, height)
        boom = Boom(context, width, height)

        highScore = readHighScore(context)
    }

    override fun run() {
        while (playing) {
            update()
            draw()
            control()
        }
    }

    fun resume() {
        playing = true
        gameThread = Thread(this)
        gameThread?.start()
    }

    fun pause() {
        playing = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun update() {
        boom.x = -300
        boom.y = -300

        for (s in stars) {
            s.update(player.speed)
        }
        for (e in enemies) {
            e.update(player.speed)
            if (Rect.intersects(player.detectCollision, e.detectCollision)) {
                boom.x = e.x
                boom.y = e.y
                e.x = -300
                lives -= 1
            }
        }

        player.update()
        warrior.update()

        score += 1
    }

    private fun draw() {
        if (surfaceHolder.surface.isValid) {
            canvas = surfaceHolder.lockCanvas()
            canvas?.drawColor(Color.BLACK)
            paint.color = Color.YELLOW
            for (star in stars) {
                paint.strokeWidth = star.starWidth.toFloat()
                canvas?.drawPoint(star.x.toFloat(), star.y.toFloat(), paint)
            }
            canvas?.drawBitmap(player.bitmap, player.x.toFloat(), player.y.toFloat(), paint)
            for (e in enemies) {
                canvas?.drawBitmap(e.bitmap, e.x.toFloat(), e.y.toFloat(), paint)
            }
            canvas?.drawBitmap(boom.bitmap, boom.x.toFloat(), boom.y.toFloat(), paint)
            canvas?.drawBitmap(warrior.bitmap, warrior.x.toFloat(), warrior.y.toFloat(), paint)
            for (i in 0 until lives) {
                canvas?.drawBitmap(
                    heartBitmap,
                    10f + i * (heartSize + 10),
                    10f,
                    paint
                )
            }
            paint.color = Color.WHITE
            paint.textSize = 42f
            canvas?.drawText("Score: $score", 10f, 100f, paint)
            surfaceHolder.unlockCanvasAndPost(canvas)
        }
    }

    private fun control() {
        Thread.sleep(17)
        if (lives <= 0) {
            playing = false
            Handler(Looper.getMainLooper()).post {
                if (!callGameOverOnce) {
                    if (score > highScore) {
                        highScore = score
                        saveHighScore(gameContext, highScore)
                    }
                    onGameOver(score)
                    callGameOverOnce = true
                }
                gameThread?.join()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                player.boosting = true
                warrior.x = event.x.toInt()
                warrior.y = event.y.toInt()
            }
            MotionEvent.ACTION_UP -> {
                player.boosting = false
            }
            MotionEvent.ACTION_MOVE -> {
                warrior.x = event.x.toInt()
                warrior.y = event.y.toInt()
            }
        }

        activePointers = event?.pointerCount ?: 0

        for (i in 0 until activePointers) {
            val pointerId = event?.getPointerId(i) ?: 0
            touchX[pointerId] = event?.getX(i) ?: -1f
            touchY[pointerId] = event?.getY(i) ?: -1f
        }

        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                touchX[pointerId] = -1f
                touchY[pointerId] = -1f
            }
            MotionEvent.ACTION_CANCEL -> {
                for (i in touchX.indices) {
                    touchX[i] = -1f
                    touchY[i] = -1f
                }
            }
        }

        return true
    }

    private fun readHighScore(context: Context): Int {
        return try {
            val fis = context.openFileInput("highscore.txt")
            val inputStreamReader = java.io.InputStreamReader(fis)
            val bufferedReader = java.io.BufferedReader(inputStreamReader)
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