package com.pepej.papi.ext

import com.pepej.papi.promise.ThreadContext
import com.pepej.papi.scheduler.Schedulers
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@ExperimentalContracts
inline fun sync(crossinline block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }
    Schedulers.sync().run {
        block()
    }
}

@ExperimentalContracts
inline fun async(crossinline block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }
    Schedulers.async().run {
        block()
    }
}

@ExperimentalContracts
inline fun onContext(crossinline block: () -> Unit, context: ThreadContext) {
    contract {
        callsInPlace(block, InvocationKind.AT_LEAST_ONCE)
    }
    Schedulers.get(context).run {
        block()
    }
}

inline fun afterSync(crossinline block: () -> Unit, delay: Long) {
    Schedulers.sync()
        .runLater({ block() }, delay)
}

inline fun afterAsync(crossinline block: () -> Unit, delay: Long) {
    Schedulers.async()
        .runLater({ block() }, delay)
}

inline fun afterOnContext(crossinline block: () -> Unit, delay: Long, context: ThreadContext) {
    Schedulers.get(context)
        .runLater({ block() }, delay)
}