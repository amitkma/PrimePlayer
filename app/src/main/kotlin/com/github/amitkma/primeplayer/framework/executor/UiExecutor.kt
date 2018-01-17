package com.github.amitkma.primeplayer.framework.executor

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

/**
 * Created by falcon on 15/1/18.
 *
 * Implementation of a [Executor] to execute a ui related task on Main thread.
 */
internal class UiExecutor : Executor {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun execute(runnable: Runnable) {
        mainThreadHandler.post(runnable)
    }

}