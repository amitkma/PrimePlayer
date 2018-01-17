package com.github.amitkma.primeplayer.injection.module

import com.github.amitkma.primeplayer.features.bookmark.BookmarkActivity
import com.github.amitkma.primeplayer.features.videos.VideoPlayerActivity
import com.github.amitkma.primeplayer.features.videos.VideosActivity
import com.github.amitkma.primeplayer.injection.scope.PerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindVideosActivity(): VideosActivity

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindVideoPlayerActivity(): VideoPlayerActivity

    @PerActivity
    @ContributesAndroidInjector()
    abstract fun bindBookmarkActivity(): BookmarkActivity

}