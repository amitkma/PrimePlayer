package com.github.amitkma.primeplayer.features.videos.domain.usecase

import com.github.amitkma.primeplayer.features.videos.data.VideosRepository
import com.github.amitkma.primeplayer.features.videos.domain.model.Video
import com.github.amitkma.primeplayer.framework.executor.Executors
import com.github.amitkma.primeplayer.framework.interactor.UseCase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by falcon on 15/1/18.
 *
 * UseCase to get videos from storage.
 */
@Singleton
class VideoUseCase
@Inject constructor(private val videosRepository: VideosRepository,
        private val executors: Executors) : UseCase<List<Video>, UseCase.None>() {

    override fun build(params: None?) {
        executors.disk().execute({
            val list = videosRepository.videos()
            if (getUseCaseCallback() != null) {
                executors.ui().execute({ getUseCaseCallback()!!.onSuccess(list) })

            }
        })
    }
}