package com.example.myproject.presentation.common

import android.content.Context

fun Context.dpToPx(dp: Float): Int {
    return (dp * resources.displayMetrics.density).toInt()
}
