package io.github.caimucheng.leaf.common.mvi

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

abstract class MVIAppViewModel<S : UiState, I : UiIntent> {

    @Suppress("MemberVisibilityCanBePrivate")
    protected val viewModelScope: CoroutineScope =
        CoroutineScope(CoroutineName("AppViewModelCoroutine") + Dispatchers.Default)

    @Suppress("LeakingThis")
    private val _state: MutableStateFlow<S> = MutableStateFlow(initialValue())

    val state: StateFlow<S> = _state.asStateFlow()

    @Suppress("MemberVisibilityCanBePrivate")
    val intent: Channel<I> = Channel(Channel.UNLIMITED)

    abstract fun initialValue(): S

    init {
        viewModelScope.launch {
            intent.consumeAsFlow().collect {
                handleIntent(it, _state.value)
            }
        }
    }

    abstract fun handleIntent(intent: I, currentState: S)

    protected fun setState(state: S) {
        _state.value = state
    }

    open fun onCleared() {
        viewModelScope.cancel()
    }

}