package com.github.amitkma.primeplayer.framework.extension

import android.content.pm.PackageManager

// Function to verify runtime permission.
fun IntArray.verifyPermissions(): Boolean {
    if (this.isEmpty()) {
        return false
    }
    return this.all { it == PackageManager.PERMISSION_GRANTED }
}