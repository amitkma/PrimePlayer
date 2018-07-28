package com.github.amitkma.primeplayer.features.highlighter.domain.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by falcon on 27/2/18.
 */

@Entity(tableName = "highlighter")
data class HighlightedItem(
        @ColumnInfo(name = "path")
        var path: String,
        @ColumnInfo(name = "name")
        var name: String,
        @ColumnInfo(name = "thumbnail")
        var thumbnail: String,
        @ColumnInfo(name = "start_window")
        var startWindow: Int,
        @ColumnInfo(name = "start_position")
        var startPosition: Long,
        @ColumnInfo(name = "stop_window")
        var stopWindow: Int,
        @ColumnInfo(name = "stop_position")
        var stopPosition: Long) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}