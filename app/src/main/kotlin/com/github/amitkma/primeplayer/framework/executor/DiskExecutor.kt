package com.github.amitkma.primeplayer.framework.executor

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of a [Executor] to execute all disk related task on a single thread.
 */
internal class DiskExecutor(threadIdentifier: String) : Executor {

    private val threadPoolExecutor = Executors.newSingleThreadExecutor(
            DiskThreadFactory(threadIdentifier))

    override fun execute(runnable: Runnable) = threadPoolExecutor.execute(runnable)

    private class DiskThreadFactory(private val threadIdentifier: String) : ThreadFactory {
        private val counter = AtomicInteger()

        override fun newThread(runnable: Runnable?) =
                Thread(runnable, "thread:$threadIdentifier:${counter.incrementAndGet()}")
    }
}