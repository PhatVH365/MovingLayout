package com.example.movinglayout.ui.relativelayoutdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.movinglayout.databinding.ActivityRelativeLayoutBinding

class RelativeLayoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRelativeLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRelativeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}