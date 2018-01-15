package com.github.amitkma.primeplayer.injection.module

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.github.amitkma.primeplayer.framework.db.PrimePlayerDatabase
import com.github.amitkma.primeplayer.framework.executor.Executors
import com.github.amitkma.primeplayer.framework.executor.ThreadExecutors
import com.github.amitkma.primeplayer.injection.qualifiers.DatabaseName
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = arrayOf(ViewModelModule::class))
open class ApplicationModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context = application

    @Singleton
    @Provides
    fun provideThreadExecutors(threadExecutors: ThreadExecutors): Executors = threadExecutors

    @Singleton
    @Provides
    @DatabaseName
    fun provideDatabaseName(): String = "PrimePlayer.db"

    @Singleton
    @Provides
    fun provideDb(application: Application, @DatabaseName dbName: String) = Room.databaseBuilder(application,
            PrimePlayerDatabase::class.java, dbName).build()
}