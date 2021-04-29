package com.example.movinglayout

import android.view.View

fun applyTranslation(view: View, deltaX: Float, deltaY: Float) {
    val deltaVector = floatArrayOf(deltaX, deltaY)
    view.matrix.mapVectors(deltaVector)
    view.translationX = view.translationX + deltaVector[0]
    view.translationY = view.translationY + deltaVector[1]
}