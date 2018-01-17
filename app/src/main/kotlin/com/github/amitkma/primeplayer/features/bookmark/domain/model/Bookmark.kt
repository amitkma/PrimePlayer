package com.github.amitkma.primeplayer.features.bookmark.domain.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by falcon on 17/1/18.
 */
@Entity(tableName = "bookmark")
data class Bookmark(
        @ColumnInfo(name = "path")
        var path: String,
        @ColumnInfo(name = "name")
        var name: String,
        @ColumnInfo(name = "thumbnail")
        var thumbnail: String,
        @ColumnInfo(name = "resume_window")
        var resumeWindow: Int,
        @ColumnInfo(name = "resume_position")
        var resumePosition: Long) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}