package com.example.movinglayout.ui.recyclerview

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.movinglayout.R
import com.example.movinglayout.data.tableList
import com.example.movinglayout.databinding.FragmentRecyclerViewBinding


/**
 * A simple [Fragment] subclass.
 * Use the [RecyclerViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecyclerViewFragment : Fragment() {

    private var _binding: FragmentRecyclerViewBinding? = null
    private val binding get() = _binding!!

    lateinit var adapter: TableRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecyclerViewBinding.inflate(inflater, container, false)

        adapter = TableRecyclerAdapter(
            ItemListener({ table ->
                Toast.makeText(
                    requireContext(),
                    "Table ${table.name} with id ${table.id} clicked",
                    Toast.LENGTH_SHORT
                ).show()
            },
                { v, t ->
                    Toast.makeText(
                        requireContext(),
                        "Table ${t.name} with id ${t.id} long clicked",
                        Toast.LENGTH_SHORT
                    ).show()

                    val item = ClipData.Item(t.name)

                    val dragData = ClipData(
                        t.name,
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
                        item
                    )

                    val myShadow = MyDragShadowBuilder(v)

                    v.startDragAndDrop(dragData, myShadow, null, 0)
                    true
                },
                { _, _, _ ->
                    false
                })
        )
        adapter.submitList(tableList)

        binding.tableList.adapter = adapter

        return binding.root
    }
}

class MyDragShadowBuilder(v: View) : View.DragShadowBuilder(v) {

    private val shadow = ColorDrawable(Color.LTGRAY)

    // Defines a callback that sends the drag shadow dimensions and touch point back to the
    // system.
    override fun onProvideShadowMetrics(size: Point, touch: Point) {
        // Sets the width of the shadow to half the width of the original View
        val width: Int = view.width / 2

        // Sets the height of the shadow to half the height of the original View
        val height: Int = view.height / 2

        // The drag shadow is a ColorDrawable. This sets its dimensions to be the same as the
        // Canvas that the system will provide. As a result, the drag shadow will fill the
        // Canvas.
        shadow.setBounds(0, 0, width, height)

        // Sets the size parameter's width and height values. These get back to the system
        // through the size parameter.
        size.set(width, height)

        // Sets the touch point's position to be in the middle of the drag shadow
        touch.set(width / 2, height / 2)
    }

    // Defines a callback that draws the drag shadow in a Canvas that the system constructs
    // from the dimensions passed in onProvideShadowMetrics().
    override fun onDrawShadow(canvas: Canvas) {
        // Draws the ColorDrawable in the Canvas passed in from the system.
        shadow.draw(canvas)
    }
}