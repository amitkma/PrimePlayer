package com.github.amitkma.primeplayer.features.bookmarks.domain.usecase

import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem
import com.github.amitkma.primeplayer.features.notepad.domain.model.Note
import com.github.amitkma.primeplayer.framework.db.PrimePlayerDatabase
import com.github.amitkma.primeplayer.framework.executor.Executors
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UseCase to get videos from storage.
 */
@Singleton
class GetNoteById
@Inject constructor(private val database: PrimePlayerDatabase,
        private val executors: Executors) : UseCase<Note, Int>() {

    override fun build(id: Int?) {
        executors.disk().execute({
            val list = database.notepadDao().loadNoteById(id!!)
            if (getUseCaseCallback() != null) {
                executors.ui().execute({ getUseCaseCallback()!!.onSuccess(list) })
            }
        })
    }
}