package com.github.amitkma.primeplayer.framework.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.github.amitkma.primeplayer.data.source.BookmarkDao
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark

/**
 * Created by falcon on 15/1/18.
 *
 * Persistent Database which is implemented using [RoomDatabase]
 */
@Database(entities = [(Bookmark::class)], version = 1, exportSchema = false)
abstract class PrimePlayerDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao
}