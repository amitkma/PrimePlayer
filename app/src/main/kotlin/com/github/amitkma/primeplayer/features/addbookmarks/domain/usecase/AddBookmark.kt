package com.github.amitkma.primeplayer.features.addbookmarks.domain.usecase

import com.github.amitkma.primeplayer.features.addbookmarks.domain.model.BookmarkParam
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.framework.db.PrimePlayerDatabase
import com.github.amitkma.primeplayer.framework.executor.Executors
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by falcon on 17/1/18.
 */
@Singleton
class AddBookmark
@Inject constructor(private val database: PrimePlayerDatabase,
        private val executors: Executors) :
        UseCase<UseCase.None, Bookmark>() {

    override fun build(params: Bookmark?) {
        executors.disk().execute({
            if (params != null) {
                database.bookmarkDao().insertBookmark(params)
                executors.ui().execute({ getUseCaseCallback()!!.onSuccess(None()) })
            } else {
                executors.ui().execute({ getUseCaseCallback()!!.onError("params can not be null") })
            }
        })
    }
}