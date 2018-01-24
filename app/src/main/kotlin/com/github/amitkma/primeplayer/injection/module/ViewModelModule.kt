package com.github.amitkma.primeplayer.injection.module

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.github.amitkma.primeplayer.features.bookmarks.BookmarkViewModel
import com.github.amitkma.primeplayer.features.videoplayer.VideoPlayerViewModel
import com.github.amitkma.primeplayer.features.videos.VideoViewModel
import com.github.amitkma.primeplayer.framework.viewmodel.PrimePlayerViewModelFactory
import com.github.amitkma.primeplayer.injection.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Created by falcon on 15/1/18.
 *
 * Module to bind ViewModels into a Map using [ViewModelKey]
 */
@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(VideoViewModel::class)
    abstract fun bindVideoViewModel(videoViewModel: VideoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BookmarkViewModel::class)
    abstract fun bindBookmarkViewModel(bookmarkViewModel: BookmarkViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VideoPlayerViewModel::class)
    abstract fun bindVideoPlayerViewModel(videoPlayerViewModel: VideoPlayerViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(
            primePlayerViewModelFactory: PrimePlayerViewModelFactory): ViewModelProvider.Factory
}
