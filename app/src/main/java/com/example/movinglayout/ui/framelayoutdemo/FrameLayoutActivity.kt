package com.example.movinglayout.ui.framelayoutdemo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.example.movinglayout.R
import com.example.movinglayout.applyTranslation
import com.example.movinglayout.custom.MultiTouchListener
import com.example.movinglayout.data.tableList
import com.example.movinglayout.model.Table
import kotlinx.android.synthetic.main.activity_frame_layout.*
import kotlinx.android.synthetic.main.activity_frame_layout.outerLayout
import kotlinx.android.synthetic.main.activity_frame_layout.view.*
import kotlinx.android.synthetic.main.table_card_item.view.*
import timber.log.Timber
import kotlin.math.floor
import kotlin.math.roundToInt

var isInitiated = false

class FrameLayoutActivity : AppCompatActivity() {

    //đống kích thước để tính toán
    private var density: Float = 0f
    private var widthPx: Int = 0
    private var heightPx: Int = 0
    private var columnNum: Int = 9
    private var itemWidthPx: Int = 0
    private var itemHeightPx: Int = 0
    private var itemSpaceWidthPx: Int = 0
    private var itemSpaceHeightPx: Int = 0
    private var scaleFactor: Float = 0f

    //biến để tính toán vị trí
    companion object {
        private const val INVALID_POINTER_ID = -1
    }

    private var activePointerId = INVALID_POINTER_ID
    private var prevX = 0f
    private var prevY = 0f


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        setContentView(R.layout.activity_frame_layout)

        initMeasurement()
        logMeasurementInfo()

        val touchListener = MultiTouchListener()
        touchListener.isRotateEnabled = false

        outerLayout.apply {
            post {
                if (!isInitiated) {
                    initItemsToGridFirstTime(this)
                    isInitiated = true
                } else {
                    this.removeAllViews()
                    initItemsToGrid(this)
                }
                touchListener.minimumScale = if (widthPx <= heightPx) this@FrameLayoutActivity.layoutContainer.width.toFloat() / width.toFloat() else this@FrameLayoutActivity.layoutContainer.height.toFloat() / height.toFloat()
            }

            setOnClickListener {
                logMeasurementInfo()
            }

            setOnTouchListener(touchListener)
        }

        coorBtn.setOnClickListener {
            Timber.i("coordinate = (${outerLayout.x}, ${outerLayout.y})")
        }

        pivotBtn.setOnClickListener {
            Timber.i("pivot = (${outerLayout.pivotX}, ${outerLayout.pivotY})")
        }

        resetPivotBtn.setOnClickListener {
            outerLayout.pivotX = 0f
            outerLayout.pivotY = 0f
            Timber.i("new pivot = (${outerLayout.pivotX}, ${outerLayout.pivotY})")
        }

        resetPosBtn.setOnClickListener {
            outerLayout.pivotX = 0f
            outerLayout.pivotY = 0f
            outerLayout.x = 0f
            outerLayout.y = 0f
        }

        sizeBtn.setOnClickListener {
            Timber.i("size = (${outerLayout.width}, ${outerLayout.height})")
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
        val layoutWidth = resources.getDimensionPixelSize(R.dimen.layout_width)
        val layoutHeight = resources.getDimensionPixelSize(R.dimen.layout_height)
        density = resources.displayMetrics.density

        widthPx = resources.displayMetrics.widthPixels
        heightPx = resources.displayMetrics.heightPixels

        itemWidthPx = resources.getDimensionPixelSize(R.dimen.item_table_width)
        itemHeightPx = resources.getDimensionPixelSize(R.dimen.item_table_height)

        itemSpaceWidthPx = resources.getDimensionPixelSize(R.dimen.item_space_width)
        itemSpaceHeightPx = resources.getDimensionPixelSize(R.dimen.item_space_height)

        scaleFactor =
            if (widthPx <= heightPx) widthPx.toFloat() / layoutWidth.toFloat() else heightPx.toFloat() / layoutHeight.toFloat()
    }

    @SuppressLint("InflateParams")
    private fun initItemsToGridFirstTime(l: FrameLayout) {
        var blockX = 0
        var blockY = 0

        for (table in tableList) {
            val pos = getMarginBlock(blockX, blockY)
            val lInflater = LayoutInflater.from(this)
            val layout = lInflater.inflate(R.layout.table_card_item, null)
            l.addView(populateViewInfo(layout, table, blockX, blockY))
            table.x = pos[0]
            table.y = pos[1]
            Timber.i("${table.name} = ($blockX, $blockY)")
            if (blockX >= itemSpaceWidthPx * columnNum) {
                blockX = 0
                blockY += itemSpaceHeightPx
            } else {
                blockX += itemSpaceWidthPx
            }
        }
    }

    private fun initItemsToGrid(l: FrameLayout) {
        for (table in tableList) {
            val lInflater = LayoutInflater.from(this)
            val layout = lInflater.inflate(R.layout.table_card_item, null)
            l.addView(
                populateViewInfo(
                    layout,
                    table,
                    table.x.toInt() * itemSpaceWidthPx.toInt(),
                    table.y.toInt() * itemSpaceHeightPx.toInt()
                )
            )
        }
    }

    private fun getMarginBlock(x: Int, y: Int): IntArray =
        intArrayOf(
            floor(x.toDouble() / itemSpaceWidthPx).toInt(),
            floor(y.toDouble() / itemSpaceHeightPx).toInt()
        )

    private fun createLayoutParams(): FrameLayout.LayoutParams {
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        params.topMargin = dpToPx(5f)
        params.leftMargin = dpToPx(5f)

        return params
    }

    @SuppressLint("ClickableViewAccessibility")
    fun populateViewInfo(view: View, table: Table, x: Int, y: Int): View {
        view.cardTableName.apply {
            text = table.name
            isSelected = true
        }
        view.cardTableId.apply {
            text = table.id.toString()
            isSelected = true
        }
        view.layoutParams = createLayoutParams()
        view.x = x.toFloat()
        view.y = y.toFloat()
        view.setOnTouchListener { v, event ->
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
                    if (postPos[0] < 0) postPos[0] = 0
                    if (postPos[0] > columnNum) postPos[0] = columnNum
                    if (postPos[1] < 0) postPos[1] = 0
                    v.x = postPos[0] * itemSpaceWidthPx.toFloat() + dpToPx(5f)
                    v.y = postPos[1] * itemSpaceHeightPx.toFloat() + dpToPx(5f)
                    if (table.x.toInt() == postPos[0] && table.y.toInt() == postPos[1]) {
                        Toast.makeText(this, "This is table ${table.name}", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        table.x = postPos[0]
                        table.y = postPos[1]
                    }
                    Timber.i("${v.x}, ${v.y}")
                    activePointerId = INVALID_POINTER_ID
                }
            }
            true
        }
        return view
    }

    private fun logMeasurementInfo() {
        Timber.i(
            """
            density = $density
            widthPx = $widthPx
            heightPx = $heightPx
            columnNum = $columnNum
            columnWidthPx = $itemWidthPx
            columnHeightPx = $itemHeightPx
            scaleFactor = $scaleFactor
        """
        )
    }

    private fun dpToPx(x: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, resources.displayMetrics)
            .roundToInt()

}