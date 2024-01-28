package io.github.caimucheng.leaf.ide.viewmodel

import io.github.caimucheng.leaf.common.mvi.MVIAppViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.ide.manager.ProjectManager
import io.github.caimucheng.leaf.ide.model.Project
import java.io.File

enum class ProjectState {
    Exit, Free, Loading, Error, Loaded
}

data class ProjectEditorState(
    val projectState: ProjectState = ProjectState.Free,
    val project: Project? = null,
    val fileTabList: MutableList<File> = mutableListOf(),
    val fileTabIndex: Int = -1
) : UiState()

sealed class ProjectEditorIntent : UiIntent() {
    data class OpenProject(
        val projectPath: String?
    ) : ProjectEditorIntent()

    data object CloseProject : ProjectEditorIntent()

    data class OpenFile(val file: File) : ProjectEditorIntent()
}

object ProjectEditorViewModel : MVIAppViewModel<ProjectEditorState, ProjectEditorIntent>() {
    override fun initialValue(): ProjectEditorState {
        return ProjectEditorState()
    }

    override fun handleIntent(intent: ProjectEditorIntent, currentState: ProjectEditorState) {
        when (intent) {
            is ProjectEditorIntent.OpenProject -> {
                setState(
                    state.value.copy(
                        projectState = ProjectState.Loading
                    )
                )
                val project = ProjectManager.filterProject(intent.projectPath)
                if (project == null) {
                    setState(
                        state.value.copy(
                            projectState = ProjectState.Error
                        )
                    )
                    return
                }
                setState(
                    state.value.copy(
                        projectState = ProjectState.Loaded,
                        project = project
                    )
                )
            }

            ProjectEditorIntent.CloseProject -> {
                setState(
                    state.value.copy(
                        project = null,
                        projectState = ProjectState.Free
                    )
                )
                // do something
                setState(
                    state.value.copy(
                        projectState = ProjectState.Exit
                    )
                )
            }

            is ProjectEditorIntent.OpenFile -> {}
        }
    }
}