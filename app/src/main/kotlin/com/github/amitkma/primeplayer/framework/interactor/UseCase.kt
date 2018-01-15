package com.github.amitkma.primeplayer.framework.interactor

import com.github.amitkma.primeplayer.framework.executor.ThreadExecutors
import java.util.concurrent.Executor
import javax.inject.Inject

/**
 * Use cases are the entry points to the domain layer.
 *
 * @param Type the response type
 * @param Params the request type
 */
abstract class UseCase<Type, in Params> where Type : Any {

    abstract fun build(params: Params?)

    fun clear(){
        useCaseCallback = null
    }

    private var useCaseCallback: UseCaseCallback<Type>? = null

    fun execute(params: Params?, useCaseCallback: UseCaseCallback<Type>) {
        this.useCaseCallback = useCaseCallback
        build(params)
    }

    fun getUseCaseCallback(): UseCaseCallback<Type>?{
        return useCaseCallback
    }

    interface UseCaseCallback<in R> {
        fun onSuccess(response: R)
        fun onError(message: String)
    }

    class None
}