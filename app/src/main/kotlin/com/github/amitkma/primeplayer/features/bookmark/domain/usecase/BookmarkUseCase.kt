package com.github.amitkma.primeplayer.features.bookmark.domain.usecase

import android.arch.lifecycle.LiveData
import com.github.amitkma.primeplayer.features.bookmark.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.videos.data.VideosRepository
import com.github.amitkma.primeplayer.features.videos.domain.model.Video
import com.github.amitkma.primeplayer.framework.db.PrimePlayerDatabase
import com.github.amitkma.primeplayer.framework.executor.Executors
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UseCase to get videos from storage.
 */
@Singleton
class BookmarkUseCase
@Inject constructor(private val database: PrimePlayerDatabase,
        private val executors: Executors) : UseCase<List<Bookmark>, UseCase.None>() {

    override fun build(params: None?) {
        executors.disk().execute({
            val list = database.bookmarkDao().loadBookmarks()
            if (getUseCaseCallback() != null) {
                executors.ui().execute({ getUseCaseCallback()!!.onSuccess(list) })
            }
        })
    }
}