package com.github.amitkma.primeplayer.framework.extension

import android.content.res.Resources
import android.util.DisplayMetrics

/**
 * Created by falcon on 22/1/18.
 */
fun Int.convertToString(): String {
    val totalSeconds = this / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    if (hours > 0) {
        return "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        return "%02d:%02d".format(minutes, seconds)
    }
}

fun Float.convertToDp(): Int {
    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    return (this / (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

fun Int.convertToPx(): Float {
    val displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics
    return this * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}