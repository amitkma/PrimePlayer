package com.github.amitkma.primeplayer.injection.scope

import javax.inject.Scope

/**
 * Created by falcon on 15/1/18.
 *
 * Scope which bounds dependencies to activity lifecycle.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity