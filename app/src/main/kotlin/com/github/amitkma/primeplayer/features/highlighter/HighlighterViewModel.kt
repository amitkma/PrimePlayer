package com.github.amitkma.primeplayer.features.bookmarks

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.amitkma.primeplayer.features.bookmarks.domain.usecase.GetHighlights
import com.github.amitkma.primeplayer.features.highlighter.domain.model.HighlightedItem
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject

/**
 * Created by falcon on 17/1/18.
 *
 * ViewModel responsible for getting data from a [GetHighlights]
 * and handling events from [HighlighterActivity].
 */
class HighlighterViewModel
@Inject constructor(private val getHighlightedItems: GetHighlights) : ViewModel() {

    /**
     * [LiveData] to keep data retrieved from the [GetHighlights].
     */
    private var highlightedItemsLiveData: MutableLiveData<List<HighlightedItem>> = MutableLiveData()

    fun getHighlightedItems(): LiveData<List<HighlightedItem>> {
        return highlightedItemsLiveData
    }

    fun fetchBookmarks() {
        getHighlightedItems.execute(null, UseCaseCallbackWrapper())
    }

    override fun onCleared() {
        getHighlightedItems.clear() // Clear the reference to this ViewModel to avoid memory leakage.
        super.onCleared()
    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [GetHighlights]
     */
    inner class UseCaseCallbackWrapper : UseCase.UseCaseCallback<List<HighlightedItem>> {
        override fun onSuccess(response: List<HighlightedItem>) {
            highlightedItemsLiveData.postValue(response)
        }

        override fun onError(message: String) {
        }
    }

}