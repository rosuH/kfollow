package me.rosuh.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.RestrictsSuspension

/**
 * A class that represents a value of one of two possible types (a disjoint union).
 *
 * mimic the Arrow's Either class ;)
 */
sealed class Either<out E, out A>() {
    abstract val isRight: Boolean
    abstract val isLeft: Boolean

    class Left<out E>(val value: E) : Either<E, Nothing>() {
        override val isRight: Boolean = false
        override val isLeft: Boolean = true
    }

    class Right<out A>(val value: A) : Either<Nothing, A>() {
        override val isRight: Boolean = true
        override val isLeft: Boolean = false
    }
}

@OptIn(ExperimentalContracts::class)
suspend fun <Error, A> either(block: suspend () -> A): Either<Error, A> {
    contract {
        callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }
    runCatching {
        return Either.Right(block() as A)
    }.getOrElse {
        return Either.Left(it as Error)
    }
}

suspend fun catchIO(
    block: suspend () -> Unit
): Either<Throwable, Unit> = withContext(Dispatchers.IO) {
    either(block)
}

@OptIn(ExperimentalContracts::class)
suspend fun <E, A> Either<E, A>.fold(
    onSuccess: suspend (A) -> Unit,
    onFailure: suspend (Throwable) -> Unit
) {
    contract {
        callsInPlace(onSuccess, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
        callsInPlace(onFailure, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }
    if (this.isRight) {
        onSuccess((this as Either.Right<A>).value)
    } else {
        onFailure((this as Either.Left<Throwable>).value)
    }
}