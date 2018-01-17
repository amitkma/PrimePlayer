package com.github.amitkma.primeplayer.features.bookmark.domain.usecase

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.ColumnInfo
import com.github.amitkma.primeplayer.features.bookmark.domain.model.Bookmark
import com.github.amitkma.primeplayer.framework.db.PrimePlayerDatabase
import com.github.amitkma.primeplayer.framework.executor.Executors
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by falcon on 17/1/18.
 */
@Singleton
class AddBookmarkUseCase
@Inject constructor(private val database: PrimePlayerDatabase,
        private val executors: Executors) : UseCase<UseCase.None, AddBookmarkUseCase.BookmarkParam>() {

    override fun build(params: AddBookmarkUseCase.BookmarkParam?) {
        executors.disk().execute({
            if (params != null) {
                val rowId = database.bookmarkDao().insertBookmark(Bookmark(
                        params.path,
                        params.name,
                        params.thumbnail,
                        params.resumeWindow,
                        params.resumePosition
                ))
                executors.ui().execute({ getUseCaseCallback()!!.onSuccess(None()) })
            } else {
                executors.ui().execute({ getUseCaseCallback()!!.onError("params can not be null") })
            }
        })
    }

    data class BookmarkParam(
            var path: String,
            var name: String,
            var thumbnail: String,
            var resumeWindow: Int,
            var resumePosition: Long) {

    }
}