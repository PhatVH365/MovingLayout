package com.example.movinglayout.ui

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.example.movinglayout.databinding.ActivityMainBinding
import com.example.movinglayout.ui.framelayoutdemo.FrameLayoutActivity
import com.example.movinglayout.ui.recyclerviewlayout.RecyclerViewLayoutActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewDemoBtn.setOnClickListener {
            val intent = Intent(this, RecyclerViewLayoutActivity::class.java)
            startActivity(intent)
        }

        binding.frameLayoutBtn.setOnClickListener {
            val intent = Intent(this, FrameLayoutActivity::class.java)
            startActivity(intent)
        }
    }
}