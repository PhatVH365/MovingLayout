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
import timber.log.Timber

class RecyclerViewLayoutActivity : AppCompatActivity() {

//    v.y = e.rawY - v.height / 2
//    v.x = e.rawX - v.width / 2

    lateinit var binding: ActivityRecyclerViewLayoutBinding
    lateinit var adapter: TableRecyclerAdapter
    private var isMoveMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerViewLayoutBinding.inflate(layoutInflater)
        supportActionBar!!.hide()
        setContentView(binding.root)

        val layoutManager = GridLayoutManager(this, 3)

        adapter = returnAdapter(isMoveMode)

        adapter.submitList(tableList)

        binding.tableList.adapter = adapter
        binding.tableList.layoutManager = layoutManager

        binding.editBtn.setOnClickListener {
            isMoveMode = when(isMoveMode) {
                true -> false
                false -> true
            }
            Timber.i("Set edit to $isMoveMode")
            binding.tableList.adapter = returnAdapter(isMoveMode)
        }
    }

    private fun returnAdapter(isMoveMode: Boolean): TableRecyclerAdapter {
        return if (isMoveMode) {
            TableRecyclerAdapter(
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
        } else {
            TableRecyclerAdapter(
                ItemListener(
                    { t ->
                        Toast.makeText(this, t.name, Toast.LENGTH_SHORT).show()
                    },
                    { _, _ ->
                        false
                    },
                    { _, _, _ ->
                        false
                    })
            )
        }
    }
}