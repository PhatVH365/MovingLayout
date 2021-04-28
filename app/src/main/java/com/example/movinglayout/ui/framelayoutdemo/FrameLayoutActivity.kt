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
import com.example.movinglayout.data.tableList
import com.example.movinglayout.databinding.ActivityFrameLayoutBinding
import com.example.movinglayout.model.Table
import timber.log.Timber
import kotlin.math.floor
import kotlin.math.roundToInt

var isInitiated = false

class FrameLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFrameLayoutBinding
    private lateinit var resource: Resources
    var density: Float = 0f
    var maxXpx: Int = 0
    var maxYpx: Int = 0
    var maxXdp: Float = 0f
    var maxYdp: Float = 0f
    var marginXpx = 0
    var marginYpx = 0
    var marginXdp = 0f
    var marginYdp = 0f


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

        initMeasurement()

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
            layoutX = ${binding.layout.height}
            layoutY = ${binding.layout.width}
        """
        )

        if (isInitiated == false) {
            initItemsToGridFirstTime()
            isInitiated = true
        } else {
            initItemsToGrid()
        }

        binding.button.setOnClickListener {
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
            layoutX = ${binding.layout.height}
            layoutY = ${binding.layout.width}
        """
            )
        }

        binding.layout.setOnClickListener {
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
            layoutX = ${binding.layout.height}
            layoutY = ${binding.layout.width}
        """
            )
        }

        binding.button2.setOnClickListener {
            binding.button.visibility = View.GONE
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
        initItemsToGrid()
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

    fun initMeasurement() {
        density = resource.displayMetrics.density

        maxXpx = resource.displayMetrics.widthPixels
        maxYpx = resource.displayMetrics.heightPixels

        maxXdp = maxXpx / density
        maxYdp = maxXpx / density

        marginXpx = maxXpx / 10
        marginYpx = maxYpx / 10
        marginXdp = maxXdp / 10
        marginYdp = maxYdp / 10
    }

    fun initItemsToGridFirstTime() {
        var blockX = 0
        var blockY = 0

        for (table in tableList) {
            val pos = getMarginBlock(blockX, blockY)
            val view = TextView(this)
            binding.layout.addView(view.drawViewToAdd(table, blockX, blockY))
            table.coorX = pos[0]
            table.coorY = pos[1]
            if (blockX >= marginXpx * 9) {
                blockX = 0
                blockY += marginYpx
            } else {
                blockX += marginXpx
            }
        }
    }

    fun initItemsToGrid() {
        binding.layout.removeAllViews()
        for (table in tableList) {
            val view = TextView(this)
            binding.layout.addView(
                view.drawViewToAdd(
                    table,
                    table.coorX * marginXpx,
                    table.coorY * marginYpx
                )
            )
        }
    }

    fun getMarginBlock(x: Int, y: Int): IntArray =
        intArrayOf(floor(x.toDouble() / marginXpx).toInt(), floor(y.toDouble() / marginYpx).toInt())

    fun createLayoutParams(x: Int, y: Int): FrameLayout.LayoutParams {
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
        this.width = marginXpx
        this.height = marginYpx
        this.text = table.name
        this.background = ContextCompat.getDrawable(context, R.drawable.item_view_bg)
        this.layoutParams = createLayoutParams(x, y)
        this.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_MOVE -> {
                    v.y = event.rawY - v.height - (maxYpx - binding.layout.height)*2
                    v.x = event.rawX - v.width / 2
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val postPos = getMarginBlock(
                        floor(v.x + v.width / 2).toInt(),
                        floor(v.y + v.height / 2).toInt()
                    )
                    Toast.makeText(context, "x = ${postPos[0]}, y = ${postPos[1]}", Toast.LENGTH_SHORT)
                        .show()
                    if (postPos[1] < 0) postPos[1] = 0
                    v.x = postPos[0] * marginXpx.toFloat()
                    v.y = postPos[1] * marginYpx.toFloat()
                    Timber.i("${v.x}, ${v.y}")
                    table.coorX = postPos[0]
                    table.coorY = postPos[1]
                    true
                }
                else -> true
            }
        }
        return this
    }

    fun dpToPx(x: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, resource.displayMetrics)
            .roundToInt()


}