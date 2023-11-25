package io.github.caimucheng.leaf.ide.viewmodel

import io.github.caimucheng.leaf.common.mvi.MVIAppViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.ide.depository.AppDepository
import io.github.caimucheng.leaf.ide.model.Plugin
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.model.isEnabled
import io.github.caimucheng.leaf.ide.model.isSupported
import kotlinx.coroutines.launch

enum class PluginState {
    Loading, Done
}

enum class ProjectState {
    Loading, Done
}

data class AppState(
    val pluginState: PluginState = PluginState.Done,
    val plugins: List<Plugin> = emptyList(),
    val projectState: ProjectState = ProjectState.Done,
    val projects: List<Project> = emptyList(),
    val isRefreshed: Boolean = false
) : UiState()

sealed class AppIntent : UiIntent() {
    data object Refresh : AppIntent()
}

object AppViewModel : MVIAppViewModel<AppState, AppIntent>() {

    private val appDepository: AppDepository = AppDepository()

    override fun initialValue(): AppState {
        return AppState()
    }

    override fun handleIntent(intent: AppIntent, currentState: AppState) {
        when (intent) {
            AppIntent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    pluginState = PluginState.Loading,
                    projectState = ProjectState.Loading
                )
            )
            val plugins = appDepository.refreshPlugins()
            val projects =
                appDepository.refreshProjects(plugins.filter { it.isSupported && it.isEnabled })
            setState(
                state.value.copy(
                    pluginState = PluginState.Done,
                    plugins = plugins,
                    projectState = ProjectState.Done,
                    projects = projects,
                    isRefreshed = true
                )
            )
        }
    }

}