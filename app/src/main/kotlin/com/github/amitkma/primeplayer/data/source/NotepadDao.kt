package com.github.amitkma.primeplayer.data.source

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem
import com.github.amitkma.primeplayer.features.notepad.domain.model.Note

/**
 * Created by falcon on 17/1/18.
 */
@Dao
interface NotepadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(note: Note)

    @Query("SELECT * FROM notes")
    fun loadNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun loadNoteById(id: Int): Note
}