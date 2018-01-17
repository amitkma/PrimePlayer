package com.github.amitkma.primeplayer.framework.executor

import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by falcon on 15/1/18.
 *
 * Implementation of a [Executor] to execute a task on a thread from Network Thread Pool.
 */
internal class NetworkExecutor(numberOfThreads: Int, threadIdentifier: String) : Executor {

    private val threadPoolExecutor = Executors.newFixedThreadPool(numberOfThreads,
            NetworkThreadFactory(threadIdentifier))

    override fun execute(runnable: Runnable) = threadPoolExecutor.execute(runnable)

    private class NetworkThreadFactory(private val threadIdentifier: String) : ThreadFactory {
        private val counter = AtomicInteger()

        override fun newThread(runnable: Runnable?) =
                Thread(runnable, "thread:$threadIdentifier:${counter.incrementAndGet()}")
    }
}