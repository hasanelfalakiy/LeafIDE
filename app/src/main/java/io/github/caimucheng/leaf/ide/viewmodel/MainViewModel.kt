package io.github.caimucheng.leaf.ide.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.caimucheng.leaf.common.mvi.MVIViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.ide.depository.MainDepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class MainState(
    val isLoadingPage: Boolean = true,
    val previousState: MainState? = null,
) : UiState()

sealed class MainIntent : UiIntent() {
    data object Initialize : MainIntent()
}

class MainViewModel : MVIViewModel<MainState, MainIntent>() {

    private val mainDepository: MainDepository = MainDepository()

    init {
        viewModelScope.launch {
            AppViewModel.state.collectLatest {
                setState(
                    state.value.copy(
                        isLoadingPage = it.pluginState === PluginState.Loading,
                        previousState = state.value
                    )
                )
            }
        }
    }

    override fun initialValue(): MainState {
        return MainState()
    }

    override fun handleIntent(intent: MainIntent, currentState: MainState) {
        when (intent) {
            MainIntent.Initialize -> initialize()
        }
    }

    private fun initialize() {
        viewModelScope.launch {
            if (!state.value.isLoadingPage) {
                setState(state.value.copy(isLoadingPage = true, previousState = state.value))
            }
            mainDepository.initialize()
            setState(state.value.copy(isLoadingPage = false, previousState = state.value))
        }
    }

}