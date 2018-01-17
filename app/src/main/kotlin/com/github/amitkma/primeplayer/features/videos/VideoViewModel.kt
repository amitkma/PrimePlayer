package com.github.amitkma.primeplayer.features.videos

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.amitkma.primeplayer.features.videos.domain.model.Video
import com.github.amitkma.primeplayer.features.videos.domain.usecase.VideoUseCase
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import com.github.amitkma.primeplayer.framework.vo.Resource
import com.github.amitkma.primeplayer.framework.vo.ResourceState
import javax.inject.Inject

/**
 * Created by falcon on 15/1/18.
 *
 * ViewModel responsible for getting data from a [VideoUseCase]
 * and handling events from [VideosActivity].
 */
class VideoViewModel
@Inject constructor(private val videoUseCase: VideoUseCase) : ViewModel() {

    /**
     * [MutableLiveData] to keep data retrieved from the [VideoUseCase].
     */
    private val videosLiveData: MutableLiveData<Resource<List<Video>>> = MutableLiveData()

    fun getVideos(): LiveData<Resource<List<Video>>> {
        if (videosLiveData.value == null) {
            fetchVideos()
        }
        return videosLiveData
    }

    private fun fetchVideos() {
        videosLiveData.postValue(Resource(ResourceState.LOADING, null, null))
        videoUseCase.execute(null, UseCaseCallbackWrapper())
    }

    override fun onCleared() {
        videoUseCase.clear() // Clear the reference to this ViewModel to avoid memory leakage.
        super.onCleared()
    }

    /**
     * Wrapper of [UseCase.UseCaseCallback] to listen from [VideoUseCase]
     */
    inner class UseCaseCallbackWrapper : UseCase.UseCaseCallback<List<Video>> {
        override fun onSuccess(response: List<Video>) {
            videosLiveData.postValue(Resource(ResourceState.SUCCESS, response, null))
        }

        override fun onError(message: String) {
            videosLiveData.postValue(Resource(ResourceState.ERROR, null, message))
        }

    }

}