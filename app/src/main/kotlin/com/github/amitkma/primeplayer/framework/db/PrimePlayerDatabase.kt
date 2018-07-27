package com.github.amitkma.primeplayer.framework.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.github.amitkma.primeplayer.data.source.BookmarkDao
import com.github.amitkma.primeplayer.data.source.HighlighterDao
import com.github.amitkma.primeplayer.data.source.NotepadDao
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem
import com.github.amitkma.primeplayer.features.notepad.domain.model.Note

/**
 * Created by falcon on 15/1/18.
 *
 * Persistent Database which is implemented using [RoomDatabase]
 */
@Database(entities = [(Bookmark::class), (HighlightedItem::class), (Note::class)], version = 3, exportSchema = false)
abstract class PrimePlayerDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao

    abstract fun highlighterDao(): HighlighterDao

    abstract fun notepadDao(): NotepadDao
}
