package com.github.amitkma.primeplayer.features.addhighlight.domain.usecase

import com.github.amitkma.primeplayer.features.addbookmarks.domain.model.BookmarkParam
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem
import com.github.amitkma.primeplayer.framework.db.PrimePlayerDatabase
import com.github.amitkma.primeplayer.framework.executor.Executors
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by falcon on 17/1/18.
 */
@Singleton
class AddHighlight
@Inject constructor(private val database: PrimePlayerDatabase,
        private val executors: Executors) :
        UseCase<UseCase.None, HighlightedItem>() {

    override fun build(params: HighlightedItem?) {
        executors.disk().execute({
            if (params != null) {
                database.highlighterDao().insertHighlightedItem(params)
                executors.ui().execute({ getUseCaseCallback()!!.onSuccess(None()) })
            } else {
                executors.ui().execute({ getUseCaseCallback()!!.onError("params can not be null") })
            }
        })
    }
}