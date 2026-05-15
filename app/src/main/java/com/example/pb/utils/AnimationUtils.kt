package com.example.pb.utils

import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes

fun View.startAnim(@AnimRes animRes: Int) {
    startAnimation(AnimationUtils.loadAnimation(context, animRes))
}
