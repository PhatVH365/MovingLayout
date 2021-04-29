package com.example.movinglayout.ui.framelayoutdemo

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.movinglayout.R
import com.example.movinglayout.applyTranslation
import com.example.movinglayout.data.tableList
import com.example.movinglayout.databinding.ActivityFrameLayoutBinding
import com.example.movinglayout.model.Table
import kotlinx.android.synthetic.main.activity_frame_layout.*
import timber.log.Timber
import kotlin.math.floor
import kotlin.math.roundToInt

var isInitiated = false

class FrameLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFrameLayoutBinding

    //lấy resource
    private lateinit var resource: Resources

    //đống kích thước để tính toán
    var density: Float = 0f
    var maxXpx: Int = 0
    var maxYpx: Int = 0
    var maxXdp: Float = 0f
    var maxYdp: Float = 0f
    var marginXpx = 0
    var marginYpx = 0
    var marginXdp = 0f
    var marginYdp = 0f
    var layoutXpx = 0
    var layoutYpx = 0
    var layoutMarginXpx = 0
    var layoutMarginYpx = 0

    //biến để tính toán vị trí
    companion object {
        private const val INVALID_POINTER_ID = -1
    }
    private var activePointerId = INVALID_POINTER_ID
    private var prevX  = 0f
    private var prevY = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrameLayoutBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
            // Note that system bars will only be "visible" if none of the
            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                hideSystemUI()
                // adjustments to your UI, such as showing the action bar or
                // other navigational controls.
            }
        }
        setContentView(binding.root)

        resource = Resources.getSystem()

        layout.apply {
            post {
                initMeasurement()
                layoutXpx = this.width
                layoutYpx = this.height
                layoutMarginXpx = layoutXpx / 10
                layoutMarginYpx = layoutYpx / 10

                logMeasurementInfo()

                if (!isInitiated) {
                    initItemsToGridFirstTime()
                    isInitiated = true
                } else {
                    layout.removeAllViews()
                    initItemsToGrid()
                }

            }

            setOnClickListener {
                logMeasurementInfo()
            }
        }

        button.setOnClickListener {
            logMeasurementInfo()
        }

        button2.setOnClickListener {
            button.visibility = View.GONE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun initMeasurement() {
        density = resource.displayMetrics.density

        maxXpx = resource.displayMetrics.widthPixels
        maxYpx = resource.displayMetrics.heightPixels

        maxXdp = maxXpx / density
        maxYdp = maxYpx / density

        marginXpx = maxXpx / 10
        marginYpx = maxYpx / 10
        marginXdp = maxXdp / 10
        marginYdp = maxYdp / 10
    }

    private fun FrameLayout.initItemsToGridFirstTime() {
        var blockX = 0
        var blockY = 0

        for (table in tableList) {
            val pos = getMarginBlock(blockX, blockY)
            val view = TextView(context)
            this.addView(view.drawViewToAdd(table, blockX, blockY))
            table.coorX = pos[0]
            table.coorY = pos[1]
            Timber.i("${table.name} = ($blockX, $blockY)")
            if (blockX >= layoutMarginXpx * 9) {
                blockX = 0
                blockY += layoutMarginYpx
            } else {
                blockX += layoutMarginXpx
            }
        }
    }

    private fun FrameLayout.initItemsToGrid() {
        for (table in tableList) {
            val view = TextView(context)
            this.addView(
                view.drawViewToAdd(
                    table,
                    table.coorX * this.width / 10,
                    table.coorY * this.height / 10
                )
            )
        }
    }

    private fun getMarginBlock(x: Int, y: Int): IntArray =
        intArrayOf(
            floor(x.toDouble() / layoutMarginXpx).toInt(),
            floor(y.toDouble() / layoutMarginYpx).toInt()
        )

    private fun createLayoutParams(x: Int, y: Int): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = x
        params.topMargin = y
        return params
    }

    @SuppressLint("ClickableViewAccessibility")
    fun TextView.drawViewToAdd(table: Table, x: Int, y: Int): TextView {
        this.width = layoutMarginXpx
        this.height = layoutMarginYpx
        this.text = table.name
        this.background = ContextCompat.getDrawable(context, R.drawable.item_view_bg)
        this.layoutParams = createLayoutParams(x, y)
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    prevX = event.x
                    prevY = event.y

                    activePointerId = event.getPointerId(0)
                }
                MotionEvent.ACTION_MOVE -> {
                    //tìm index của pointer đang hoạt động và lấy vị trí
                    val pointerIndex = event.findPointerIndex(activePointerId)
                    if (pointerIndex != -1) {
                        val currX = event.getX(pointerIndex)
                        val currY = event.getY(pointerIndex)

                        //di chuyển
                        applyTranslation(v, currX - prevX, currY - prevY)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val postPos = getMarginBlock(
                        floor(v.x + v.width / 2).toInt(),
                        floor(v.y + v.height / 2).toInt()
                    )
                    Timber.i("x = ${postPos[0]}, y = ${postPos[1]}")
                    if (postPos[1] < 0) postPos[1] = 0
                    if (postPos[1] > 9) postPos[1] = 9
                    v.x = postPos[0] * layoutMarginXpx.toFloat()
                    v.y = postPos[1] * layoutMarginYpx.toFloat()
                    if (table.coorX == postPos[0] && table.coorY == postPos[1]) {
                        Toast.makeText(context, "This is table ${table.name}", Toast.LENGTH_SHORT).show()
                    } else {
                        table.coorX = postPos[0]
                        table.coorY = postPos[1]
                    }
                    Timber.i("${v.x}, ${v.y}")
                    activePointerId = INVALID_POINTER_ID
                }
            }
            true
        }
        return this
    }

    private fun logMeasurementInfo() {
        Timber.i(
            """
            density = $density
            maxXpx = $maxXpx
            maxYpx = $maxYpx
            maxXdp = $maxXdp
            maxYdp = $maxYdp
            marginXpx = $marginXpx
            marginYpx = $marginYpx
            marginXdp = $marginXdp
            marginYdp = $marginYdp
            layoutX = $layoutXpx
            layoutY = $layoutYpx
            layoutMarginXpx = $layoutMarginXpx
            layoutMarginYpx = $layoutMarginYpx
            layout = (${binding.layout.width}, ${binding.layout.height})
        """
        )
    }

    fun dpToPx(x: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, resource.displayMetrics)
            .roundToInt()


}