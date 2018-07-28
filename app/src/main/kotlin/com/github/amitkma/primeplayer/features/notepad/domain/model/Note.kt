package com.github.amitkma.primeplayer.features.notepad.domain.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by falcon on 27/2/18.
 */

@Entity(tableName = "notes")
data class Note(
        @ColumnInfo(name = "title")
        var title: String,
        @ColumnInfo(name = "note_text")
        var noteText: String,
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0)