package com.github.amitkma.primeplayer.injection.module

import com.github.amitkma.primeplayer.features.videos.VideosActivity
import com.github.amitkma.primeplayer.injection.scope.PerActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBindingModule {

    @PerActivity
    @ContributesAndroidInjector
    abstract fun bindVideosActivity(): VideosActivity
}