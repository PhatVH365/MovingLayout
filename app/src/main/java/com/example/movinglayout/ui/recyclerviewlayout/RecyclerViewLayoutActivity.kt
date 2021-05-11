package com.example.movinglayout.ui.recyclerviewlayout

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movinglayout.R
import com.example.movinglayout.data.tableList
import com.example.movinglayout.databinding.ActivityRecyclerViewLayoutBinding
import com.example.movinglayout.model.Table
import com.example.movinglayout.ui.recyclerview.ItemListener
import com.example.movinglayout.ui.recyclerview.MyDragShadowBuilder
import com.example.movinglayout.ui.recyclerview.TableRecyclerAdapter
import kotlinx.android.synthetic.main.activity_recycler_view_layout.*
import timber.log.Timber

class RecyclerViewLayoutActivity : AppCompatActivity() {

//    v.y = e.rawY - v.height / 2
//    v.x = e.rawX - v.width / 2

    lateinit var adapter: TableRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_recycler_view_layout)

        val layoutManager = CustomLayoutManager(this, 3)

        adapter = TableRecyclerAdapter(
            ItemListener(
                {},
                { _, _ ->
                    false
                },
                { v, e, t ->
                    if(e.action == MotionEvent.ACTION_MOVE) {
                        v.y = e.rawY - v.height
                        v.x = e.rawX - v.width / 2
                    }
                    true
                })
        )

        adapter.submitList(tableList)

        tableRecyclerList.adapter = adapter
        tableRecyclerList.layoutManager = layoutManager
    }
}