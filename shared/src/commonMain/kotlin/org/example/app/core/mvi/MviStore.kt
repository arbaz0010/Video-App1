package org.example.app.core.mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Minimal MVI store. Subclass with your State, Intent, and Effect types,
 * implement [handleIntent], and call [updateState] / [postEffect] from there.
 */
abstract class MviStore<S, I, E>(initialState: S) {
    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effects = Channel<E>(Channel.BUFFERED)
    val effects: Flow<E> = _effects.receiveAsFlow()

    fun dispatch(intent: I) {
        scope.launch { handleIntent(_state.value, intent) }
    }

    protected abstract suspend fun handleIntent(state: S, intent: I)

    protected fun updateState(reducer: S.() -> S) = _state.update(reducer)

    protected fun postEffect(effect: E) {
        _effects.trySend(effect)
    }

    val currentState: S get() = _state.value

    fun destroy() {
        scope.coroutineContext.cancelChildren()
    }
}
