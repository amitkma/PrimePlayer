package com.github.amitkma.primeplayer.framework.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.github.amitkma.primeplayer.features.bookmark.data.BookmarkDao
import com.github.amitkma.primeplayer.features.bookmark.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.videos.domain.model.Video

/**
 * Created by falcon on 15/1/18.
 *
 * Persistent Database which is implemented using [RoomDatabase]
 */
@Database(entities = [(Bookmark::class)], version = 1, exportSchema = false)
abstract class PrimePlayerDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao
}