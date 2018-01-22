package com.github.amitkma.primeplayer.framework.extension

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