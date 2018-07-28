package com.github.amitkma.primeplayer.framework.executor

import java.util.concurrent.Executor

/**
 * Created by falcon on 15/1/18.
 *
 * Abstract interface whose implementation provides various [Executor] corresponding to a task.
 */
interface Executors {
    fun ui(): Executor
    fun disk(): Executor
    fun network(): Executor
}