package com.github.amitkma.primeplayer.injection.scope

import javax.inject.Scope

/**
 * Scope which bounds dependencies to activity lifecycle.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity