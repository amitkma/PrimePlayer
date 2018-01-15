package com.github.amitkma.primeplayer.features.videos.domain.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Video(@PrimaryKey val path: String, val thumbnail: String)