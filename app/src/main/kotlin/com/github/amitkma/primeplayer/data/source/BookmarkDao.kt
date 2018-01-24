package com.github.amitkma.primeplayer.data.source

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark

/**
 * Created by falcon on 17/1/18.
 */
@Dao
interface BookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookmark(bookmark: Bookmark)

    @Query("SELECT * FROM bookmark")
    fun loadBookmarks(): List<Bookmark>

    @Query("SELECT * FROM bookmark WHERE path = :path")
    fun loadBookmarksByPath(path: String): List<Bookmark>
}