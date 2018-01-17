package com.github.amitkma.primeplayer.features.bookmark

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.amitkma.primeplayer.features.bookmark.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.bookmark.domain.usecase.BookmarkUseCase
import com.github.amitkma.primeplayer.features.videos.domain.model.Video
import com.github.amitkma.primeplayer.features.videos.domain.usecase.VideoUseCase
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import com.github.amitkma.primeplayer.framework.vo.Resource
import com.github.amitkma.primeplayer.framework.vo.ResourceState
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel responsible for getting data from a [BookmarkUse]
 * and handling events from [BookmarkActivity].
 */
class BookmarkViewModel
@Inject constructor(private val bookmarkUseCase: BookmarkUseCase) : ViewModel() {

    /**
     * [LiveData] to keep data retrieved from the [BookmarkUseCase].
     */
    private var bookmarkLiveData: MutableLiveData<List<Bookmark>> = MutableLiveData()

    fun getBookmarks(): LiveData<List<Bookmark>> {
        if (bookmarkLiveData.value == null) {
            fetchBookmarks()
        }
        return bookmarkLiveData
    }

    private fun fetchBookmarks() {
        bookmarkUseCase.execute(null, UseCaseCallbackWrapper())
    }

    override fun onCleared() {
        bookmarkUseCase.clear() // Clear the reference to this ViewModel to avoid memory leakage.
        super.onCleared()
    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [BookmarkUseCase]
     */
    inner class UseCaseCallbackWrapper : UseCase.UseCaseCallback<List<Bookmark>> {
        override fun onSuccess(response: List<Bookmark>) {
            bookmarkLiveData.postValue(response)
        }

        override fun onError(message: String) {
        }
    }

}