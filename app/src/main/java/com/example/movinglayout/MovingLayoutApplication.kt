package com.example.movinglayout

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

class MovingLayoutApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}