package io.github.caimucheng.leaf.ide.viewmodel

import io.github.caimucheng.leaf.common.mvi.MVIAppViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.ide.manager.ModuleManager
import io.github.caimucheng.leaf.ide.manager.ProjectManager
import io.github.caimucheng.leaf.ide.model.Module
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.model.isEnabled
import kotlinx.coroutines.launch

enum class ModuleState {
    Loading, Done
}

enum class ProjectState {
    Loading, Done
}

data class AppState(
    val moduleState: ModuleState = ModuleState.Done,
    val modules: List<Module> = emptyList(),
    val projectState: ProjectState = ProjectState.Done,
    val projects: List<Project> = emptyList(),
    val isRefreshed: Boolean = false
) : UiState()

sealed class AppIntent : UiIntent() {
    data object Refresh : AppIntent()

}

object AppViewModel : MVIAppViewModel<AppState, AppIntent>() {
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
                    moduleState = ModuleState.Loading,
                    projectState = ProjectState.Loading
                )
            )
            val modules = ModuleManager.modules
            val projects = ProjectManager.filterProject(modules.filter { it.isEnabled })
            setState(
                state.value.copy(
                    moduleState = ModuleState.Done,
                    modules = modules,
                    projectState = ProjectState.Done,
                    projects = projects,
                    isRefreshed = true
                )
            )
        }
    }

}