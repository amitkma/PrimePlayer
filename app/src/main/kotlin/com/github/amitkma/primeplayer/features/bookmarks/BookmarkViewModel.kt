package com.github.amitkma.primeplayer.features.bookmarks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.bookmarks.domain.usecase.GetBookmarks
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject

/**
 * Created by falcon on 17/1/18.
 *
 * ViewModel responsible for getting data from a [GetBookmarks]
 * and handling events from [BookmarkActivity].
 */
class BookmarkViewModel
@Inject constructor(private val getBookmarks: GetBookmarks) : ViewModel() {

    /**
     * [LiveData] to keep data retrieved from the [GetBookmarks].
     */
    private var bookmarkLiveData: MutableLiveData<List<Bookmark>> = MutableLiveData()

    fun getBookmarks(): LiveData<List<Bookmark>> {
        return bookmarkLiveData
    }

    fun fetchBookmarks() {
        getBookmarks.execute(null, UseCaseCallbackWrapper())
    }

    override fun onCleared() {
        getBookmarks.clear() // Clear the reference to this ViewModel to avoid memory leakage.
        super.onCleared()
    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [GetBookmarks]
     */
    inner class UseCaseCallbackWrapper : UseCase.UseCaseCallback<List<Bookmark>> {
        override fun onSuccess(response: List<Bookmark>) {
            bookmarkLiveData.postValue(response)
        }

        override fun onError(message: String) {
        }
    }

}