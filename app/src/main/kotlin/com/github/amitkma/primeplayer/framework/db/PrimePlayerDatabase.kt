package com.github.amitkma.primeplayer.framework.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.github.amitkma.primeplayer.features.videos.domain.model.Video

/**
 * Persistent Database which is implemented using [RoomDatabase]
 */
@Database(entities = arrayOf(Video::class), version = 1, exportSchema = false)
abstract class PrimePlayerDatabase: RoomDatabase() {

}