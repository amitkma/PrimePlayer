package com.github.amitkma.primeplayer.features.bookmarks.domain.usecase

import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem
import com.github.amitkma.primeplayer.framework.db.PrimePlayerDatabase
import com.github.amitkma.primeplayer.framework.executor.Executors
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UseCase to get videos from storage.
 */
@Singleton
class GetHighlights
@Inject constructor(private val database: PrimePlayerDatabase,
        private val executors: Executors) : UseCase<List<HighlightedItem>, UseCase.None>() {

    override fun build(params: None?) {
        executors.disk().execute({
            val list = database.highlighterDao().loadHighlightedItems()
            if (getUseCaseCallback() != null) {
                executors.ui().execute({ getUseCaseCallback()!!.onSuccess(list) })
            }
        })
    }
}