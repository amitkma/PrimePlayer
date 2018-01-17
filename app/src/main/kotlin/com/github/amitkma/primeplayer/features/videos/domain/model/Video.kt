package com.github.amitkma.primeplayer.features.videos.domain.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
data class Video(val name: String, val path: String, val thumbnail: String)