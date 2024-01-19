package io.github.caimucheng.leaf.ide.viewmodel

import io.github.caimucheng.leaf.common.mvi.MVIAppViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.ide.model.Module
import io.github.caimucheng.leaf.ide.model.Project
import kotlinx.coroutines.launch

enum class ProjectStatus {
    CLEAR, FREE, LOADING, ERROR, DONE, CLOSE
}

data class ProjectEditorState(
    val projectStatus: ProjectStatus = ProjectStatus.FREE,
    val project: Project? = null,
    val module: Module? = null,
    val editorCurrentContent: String? = null
) : UiState()

sealed class ProjectEditorIntent : UiIntent() {
    data object Clear : ProjectEditorIntent()

    data class OpenProject(val projectPath: String?) :
        ProjectEditorIntent()

    data object CloseProject : ProjectEditorIntent()
}

object ProjectEditorViewModel : MVIAppViewModel<ProjectEditorState, ProjectEditorIntent>() {
    override fun initialValue(): ProjectEditorState {
        return ProjectEditorState()
    }

    override fun handleIntent(intent: ProjectEditorIntent, currentState: ProjectEditorState) {
        when (intent) {
            ProjectEditorIntent.Clear -> {
                setState(ProjectEditorState())
            }

            is ProjectEditorIntent.OpenProject -> {
                viewModelScope.launch {
                    setState(
                        state.value.copy(
                            projectStatus = ProjectStatus.LOADING
                        )
                    )

                    val project = AppViewModel.state.value.projects.find {
                        intent.projectPath == it.projectPath
                    }
                    
                    if (project == null) {
                        setState(
                            state.value.copy(projectStatus = ProjectStatus.ERROR)
                        )
                        return@launch
                    }

                    setState(
                        state.value.copy(
                            projectStatus = ProjectStatus.DONE,
                            project = project
                        )
                    )
                }
            }

            ProjectEditorIntent.CloseProject -> {
                setState(
                    state.value.copy(
                        projectStatus = ProjectStatus.CLOSE
                    )
                )
            }

            else -> {}
        }
    }
}