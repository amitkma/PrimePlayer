package com.github.amitkma.primeplayer.injection.component

import android.app.Application
import com.github.amitkma.primeplayer.PrimePlayerApplication
import com.github.amitkma.primeplayer.injection.module.ActivityBindingModule
import com.github.amitkma.primeplayer.injection.module.ApplicationModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class,
        AndroidInjectionModule::class,
        ActivityBindingModule::class))
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }

    fun inject(application: PrimePlayerApplication)
}
