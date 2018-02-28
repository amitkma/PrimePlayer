package com.github.amitkma.primeplayer.data.source

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem

/**
 * Created by falcon on 17/1/18.
 */
@Dao
interface HighlighterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHighlightedItem(highlightedItem: HighlightedItem)

    @Query("SELECT * FROM highlighter")
    fun loadHighlightedItems(): List<HighlightedItem>

    @Query("SELECT * FROM highlighter WHERE path = :path")
    fun loadHighlightedItemsByPath(path: String): List<HighlightedItem>
}