package com.github.amitkma.primeplayer.injection.module

import com.github.amitkma.primeplayer.features.bookmarks.BookmarkActivity
import com.github.amitkma.primeplayer.features.bookmarks.HighlighterActivity
import com.github.amitkma.primeplayer.features.videoplayer.VideoPlayerActivity
import com.github.amitkma.primeplayer.features.videos.VideosActivity
import com.github.amitkma.primeplayer.injection.scope.PerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by falcon on 15/1/18.
 */
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

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindHighlighterActivity(): HighlighterActivity

}