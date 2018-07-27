package com.github.amitkma.primeplayer.features.videoplayer

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.amitkma.primeplayer.features.addbookmarks.domain.usecase.AddBookmark
import com.github.amitkma.primeplayer.features.addhighlight.domain.usecase.AddHighlight
import com.github.amitkma.primeplayer.features.bookmarks.domain.model.Bookmark
import com.github.amitkma.primeplayer.features.bookmarks.domain.usecase.GetNoteById
import com.github.amitkma.primeplayer.features.bookmarks.domain.usecase.GetNotes
import com.github.amitkma.primeplayer.features.bookmarks.domain.usecase.SaveNote
import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem
import com.github.amitkma.primeplayer.features.notepad.domain.model.Note
import com.github.amitkma.primeplayer.features.videoplayer.domain.usecase.GetVideoBookmarks
import com.github.amitkma.primeplayer.features.videoplayer.domain.usecase.GetVideoHighlights
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
        private val videoHighlights: GetVideoHighlights,
        private val addBookmark: AddBookmark,
        private val addHighlight: AddHighlight,
        private val getNotes: GetNotes,
        private val getNoteById: GetNoteById,
        private val saveNote: SaveNote) : ViewModel() {

    /**
     * [MutableLiveData] to keep data retrieved from the [VideoUseCase].
     */
    private val bookmarkLiveData: MutableLiveData<List<Bookmark>> = MutableLiveData()

    private val highlightsLiveData: MutableLiveData<List<HighlightedItem>> = MutableLiveData()

    private val notesLiveData: MutableLiveData<List<Note>> = MutableLiveData()

    private val noteLiveData: MutableLiveData<Note> = MutableLiveData()

    fun getVideoBookmarks(path: String): LiveData<List<Bookmark>> {
        fetchVideoBookmarks(path)
        return bookmarkLiveData
    }

    private fun fetchVideoBookmarks(path: String) {
        bookmarkLiveData.postValue(null)
        videoBookmarks.execute(path, GetVideoBookmarksCallback())
    }

    fun getVideoHighlights(path: String): LiveData<List<HighlightedItem>> {
        fetchVideoHighlights(path)
        return highlightsLiveData
    }

    private fun fetchVideoHighlights(path: String) {
        highlightsLiveData.postValue(null)
        videoHighlights.execute(path, GetVideoHighlightsCallback())
    }

    fun getNotes(): LiveData<List<Note>>{
        notesLiveData.postValue(null)
        fetchNotes()
        return notesLiveData
    }

    private fun fetchNotes() {
        getNotes.execute(UseCase.None(), GetNotesCallback())
    }

    fun getNoteById(id: Int): LiveData<Note>{
        fetchNoteById(id)
        return noteLiveData
    }

    private fun fetchNoteById(id: Int) {
        noteLiveData.postValue(null)
        getNoteById.execute(id, GetNoteByIdCallback())
    }

    fun addBookmark(path: String, videoName: String, thumbnail: String,
            resumeWindow: Int, resumePosition: Long) {
        addBookmark.execute(Bookmark(path, videoName, thumbnail, resumeWindow, resumePosition),
                AddBookmarkCallback())
    }

    fun addHighlight(path: String, videoName: String, thumbnail: String,
            startWindow: Int, startPosition: Long, stopWindow: Int, stopPosition: Long) {
        addHighlight.execute(
                HighlightedItem(path, videoName, thumbnail, startWindow, startPosition, stopWindow,
                        stopPosition), AddHighlighterCallback())
    }

    fun saveNote(note: Note){
        saveNote.execute(note, SaveNoteCallBack())
    }

    override fun onCleared() {
        // Clear the reference to this ViewModel to avoid memory leakage.
        videoBookmarks.clear()
        videoHighlights.clear()
        getNotes.clear()
        getNoteById.clear()
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

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [GetVideoHighlights]
     */
    inner class GetVideoHighlightsCallback : UseCase.UseCaseCallback<List<HighlightedItem>> {
        override fun onSuccess(response: List<HighlightedItem>) {
            highlightsLiveData.postValue(response)
        }

        override fun onError(message: String) {
            highlightsLiveData.postValue(emptyList())
        }

    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [GetNotes]
     */
    inner class GetNotesCallback : UseCase.UseCaseCallback<List<Note>> {
        override fun onSuccess(response: List<Note>) {
            notesLiveData.postValue(response)
        }

        override fun onError(message: String) {
            notesLiveData.postValue(emptyList())
        }

    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [GetNoteById]
     */
    inner class GetNoteByIdCallback : UseCase.UseCaseCallback<Note> {
        override fun onSuccess(response: Note) {
            noteLiveData.postValue(response)
        }

        override fun onError(message: String) {
            val note = Note("Error", "Couldn't load note because $message")
            noteLiveData.postValue(note)
        }

    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [AddBookmarkCallback]
     */
    inner class AddBookmarkCallback : UseCase.UseCaseCallback<UseCase.None> {
        override fun onSuccess(response: UseCase.None) {
        }

        override fun onError(message: String) {
        }
    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [AddHighlighterCallback]
     */
    inner class AddHighlighterCallback : UseCase.UseCaseCallback<UseCase.None> {
        override fun onSuccess(response: UseCase.None) {
        }

        override fun onError(message: String) {
        }
    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [SaveNote]
     */
    inner class SaveNoteCallBack : UseCase.UseCaseCallback<Unit> {
        override fun onSuccess(response: Unit) {
            fetchNotes()
        }

        override fun onError(message: String) {
        }

    }

}