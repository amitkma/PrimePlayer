package com.github.amitkma.primeplayer.framework.executor

import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [Executors] to provide various [Executor]
 */
@Singleton
class ThreadExecutors
@Inject constructor() : Executors {

    companion object {

        private const val NETWORK_THREADS = 6

        internal val diskExecutor: Executor
        internal val networkExecutor: Executor
        internal val uiExecutor: Executor

        init {
            diskExecutor = DiskExecutor("Disk-Thread")
            networkExecutor = NetworkExecutor(NETWORK_THREADS, "Network-Thread-Pool")
            uiExecutor = UiExecutor()
        }
    }

    override fun ui(): Executor = uiExecutor

    override fun disk(): Executor = diskExecutor

    override fun network(): Executor = networkExecutor

}