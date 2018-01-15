package com.github.amitkma.primeplayer.framework.executor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * Implementation of a [Executor] to execute a ui related task on Main thread.
 */
internal class UiExecutor : Executor {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun execute(runnable: Runnable) {
        mainThreadHandler.post(runnable)
    }

}