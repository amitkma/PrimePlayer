package com.github.amitkma.primeplayer.features.videoplayer

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.amitkma.primeplayer.features.addbookmarks.domain.usecase.AddBookmark
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.videoplayer.domain.usecase.GetVideoBookmarks
import com.github.amitkma.primeplayer.features.videos.domain.usecase.VideoUseCase
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject

/**
 * Created by falcon on 15/1/18.
 *
 * ViewModel responsible for getting data from a [VideoUseCase]
 * and handling events from [VideosActivity].
 */
class VideoPlayerViewModel
@Inject constructor(private val videoBookmarks: GetVideoBookmarks,
        private val addBookmark: AddBookmark) : ViewModel() {

    /**
     * [MutableLiveData] to keep data retrieved from the [VideoUseCase].
     */
    private val bookmarkLiveData: MutableLiveData<List<Bookmark>> = MutableLiveData()

    fun getVideoBookmarks(path: String): LiveData<List<Bookmark>> {
        fetchVideoBookmarks(path)
        return bookmarkLiveData
    }

    private fun fetchVideoBookmarks(path: String) {
        bookmarkLiveData.postValue(null)
        videoBookmarks.execute(path, GetVideoBookmarksCallback())
    }

    fun addBookmark(path: String, videoName: String, thumbnail: String,
            resumeWindow: Int, resumePosition: Long) {
        addBookmark.execute(Bookmark(path, videoName, thumbnail, resumeWindow, resumePosition),
                AddBookmarkCallback())
    }

    override fun onCleared() {
        videoBookmarks.clear() // Clear the reference to this ViewModel to avoid memory leakage.
        super.onCleared()
    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [GetVideoBookmarks]
     */
    inner class GetVideoBookmarksCallback : UseCase.UseCaseCallback<List<Bookmark>> {
        override fun onSuccess(response: List<Bookmark>) {
            bookmarkLiveData.postValue(response)
        }

        override fun onError(message: String) {
            bookmarkLiveData.postValue(emptyList())
        }

    }

    inner class AddBookmarkCallback : UseCase.UseCaseCallback<UseCase.None> {
        override fun onSuccess(response: UseCase.None) {
        }

        override fun onError(message: String) {
        }
    }

}