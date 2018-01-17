package com.github.amitkma.primeplayer.framework.interactor

/**
 * Created by falcon on 15/1/18.
 *
 * Use cases are the entry points to the domain layer.
 *
 * @param Type the response type
 * @param Params the request type
 */
abstract class UseCase<Type, in Params> where Type : Any {

    abstract fun build(params: Params?)

    fun clear() {
        useCaseCallback = null
    }

    private var useCaseCallback: UseCaseCallback<Type>? = null

    fun execute(params: Params?, useCaseCallback: UseCaseCallback<Type>) {
        this.useCaseCallback = useCaseCallback
        build(params)
    }

    fun getUseCaseCallback(): UseCaseCallback<Type>? {
        return useCaseCallback
    }

    interface UseCaseCallback<in R> {
        fun onSuccess(response: R)
        fun onError(message: String)
    }

    class None
}