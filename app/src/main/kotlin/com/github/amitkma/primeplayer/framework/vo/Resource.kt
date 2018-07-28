package com.github.amitkma.primeplayer.framework.vo

import android.support.annotation.NonNull
import android.support.annotation.Nullable

/**
 * Created by falcon on 15/1/18.
 *
 * Generic class that describes a data with a status
 */
data class Resource<out T> constructor(@NonNull val state: ResourceState, @Nullable val data: T?,
        @Nullable val message: String?)