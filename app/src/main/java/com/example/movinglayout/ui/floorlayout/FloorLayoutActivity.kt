package com.example.movinglayout.ui.floorlayout

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.movinglayout.R
import com.example.movinglayout.applyTranslation
import com.example.movinglayout.custom.MultiTouchListener
import com.example.movinglayout.data.tableList
import com.example.movinglayout.model.Table
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_floor_layout.*
import kotlinx.android.synthetic.main.activity_frame_layout.*
import kotlinx.android.synthetic.main.table_card_item.view.*
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class FloorLayoutActivity : AppCompatActivity() {

    companion object {
        private const val INVALID_POINTER_ID = -1

        private const val MODE_STILL = 0
        private const val MODE_RESIZE = 1
        private const val MODE_MOVE = 2
    }

    private var density: Float = 0f
    private var widthPx: Int = 0
    private var heightPx: Int = 0
    private var columnSize: Int = 0

    private var isEditMode: Boolean = false
    private var tableEditMode = MODE_STILL
    private var activePointerId = INVALID_POINTER_ID
    private var prevX = 0f
    private var prevY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor_layout)

        initMeasurement()

        val touchListener = MultiTouchListener()
        touchListener.isRotateEnabled = false

        if (tableList.isNotEmpty()) {
            for (table in tableList) {
                addTable(table, datLayout)
            }
        }

        datLayout.apply {
            post {
                val scaleFactor =
                    if (widthPx <= heightPx) this@FloorLayoutActivity.floorLayoutContainer.width.toFloat() / width.toFloat() else this@FloorLayoutActivity.floorLayoutContainer.height.toFloat() / height.toFloat()
                touchListener.minimumScale = scaleFactor
                scaleX = scaleFactor
                scaleY = scaleFactor
                pivotX = 0f
                pivotY = 0f
                x = 0f
                y = 0f
            }

            setOnTouchListener(touchListener)
        }

        addTableBtn.setOnClickListener {
            val table = Table(tableNameEdt.text.toString(), tableIdEdt.text.toString().toInt(), 0, 0, 20, 20)
            addTable(table, datLayout)
            tableList.add(table)
        }

        editModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            isEditMode = isChecked
        }
    }

    private fun initMeasurement() {
        val layoutWidth = resources.getDimensionPixelSize(R.dimen.floor_layout_width)
        density = resources.displayMetrics.density

        widthPx = resources.displayMetrics.widthPixels
        heightPx = resources.displayMetrics.heightPixels

        columnSize = layoutWidth / 100
    }

    private fun getMarginBlock(x: Float, y: Float): IntArray =
        intArrayOf(
            floor(x.toDouble() / columnSize).toInt(),
            floor(y.toDouble() / columnSize).toInt()
        )

    private fun getTableSizeBlock(x: Float, y: Float): IntArray =
        intArrayOf(
            ceil(x.toDouble() / columnSize).toInt(),
            ceil(y.toDouble() / columnSize).toInt()
        )

    @SuppressLint("ClickableViewAccessibility")
    private fun addTable(table: Table, layout: ViewGroup) {
        val lInflater = LayoutInflater.from(this)
        val cardItem = lInflater.inflate(R.layout.table_card_item, null)

        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        cardItem.apply {

            x = table.x.toFloat() * columnSize
            y = table.y.toFloat() * columnSize

            cardTableName.apply {
                text = table.name
                isSelected = true
            }

            cardTableId.apply {
                text = table.id.toString()
                isSelected = true
            }

            tag = "table-${table.id}"

            cardTableLayout.apply {
                layoutParams = params
                params.width = table.width * columnSize
                params.height = table.height * columnSize

                setOnClickListener {
                    Toast.makeText(context, "Clicked table ${table.name}", Toast.LENGTH_SHORT).show()
                }

                setOnLongClickListener {
                    removeTable(this, table, datLayout)
                    true
                }

                setOnTouchListener{v, event ->
                    when (isEditMode) {
                        true -> {
                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    prevX = event.x
                                    prevY = event.y

                                    tableEditMode = if (((event.x - v.width <= dpToPx(50f) && event.x - v.width > 0) || (v.width - event.x <= dpToPx(
                                            50f
                                        ) && v.width - event.x > 0))
                                        && ((event.y - v.height <= dpToPx(50f) && event.y - v.height > 0) || (v.height - event.y <= dpToPx(
                                            50f
                                        ) && v.height - event.y > 0))
                                    ) {
                                        MODE_RESIZE
                                    } else {
                                        MODE_MOVE
                                    }
                                    activePointerId = event.getPointerId(0)
                                }
                                MotionEvent.ACTION_MOVE -> {
                                    when (tableEditMode) {
                                        MODE_MOVE -> {
                                            val pointerIndex = event.findPointerIndex(activePointerId)
                                            if (pointerIndex != -1) {
                                                val currX = event.getX(pointerIndex)
                                                val currY = event.getY(pointerIndex)

                                                //di chuyển
                                                applyTranslation(v, currX - prevX, currY - prevY)
                                            }
                                        }
                                        MODE_RESIZE -> {
                                            v.layoutParams.width = event.x.toInt()
                                            v.layoutParams.height = event.y.toInt()
                                            v.requestLayout()
                                        }
                                    }
                                }
                                MotionEvent.ACTION_UP -> {
                                    val postPos = getMarginBlock(v.x, v.y)
                                    val postSize = getTableSizeBlock(v.width.toFloat(), v.height.toFloat())

                                    Timber.i("x = ${postPos[0]}, y = ${postPos[1]}")

                                    if (postPos[0] < 0) postPos[0] = 0
                                    if (postPos[0] + floor(v.width / columnSize.toFloat()) > 100) postPos[0] =
                                        100 - floor(v.width / columnSize.toFloat()).toInt()
                                    if (postPos[1] < 0) postPos[1] = 0
                                    if (postPos[1] + floor(v.height / columnSize.toFloat()) > 100) postPos[1] =
                                        100 - floor(v.height / columnSize.toFloat()).toInt()

                                    v.x = postPos[0] * columnSize.toFloat()
                                    v.y = postPos[1] * columnSize.toFloat()
                                    v.layoutParams.width = postSize[0] * columnSize
                                    v.layoutParams.height = postSize[1] * columnSize
                                    v.requestLayout()

                                    table.apply {
                                        table.x = postPos[0]
                                        table.y = postPos[1]
                                        table.width = postSize[0]
                                        table.height = postSize[1]
                                    }

                                    tableEditMode = MODE_STILL
                                }
                            }
                            true
                        }
                        false -> {
                            false
                        }
                    }
                }
            }
        }

        layout.addView(cardItem)
    }

    private fun removeTable(view: View, table: Table, layout: ViewGroup) {
        layout.removeView(view)
        tableList.remove(table)
    }

    private fun dpToPx(x: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, resources.displayMetrics)
            .roundToInt()
}