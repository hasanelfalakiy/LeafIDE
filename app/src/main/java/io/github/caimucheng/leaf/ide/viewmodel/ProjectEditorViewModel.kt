package io.github.caimucheng.leaf.ide.viewmodel

import io.github.caimucheng.leaf.common.mvi.MVIAppViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.common.util.FileUtils
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.model.TreeNode
import kotlinx.coroutines.launch
import java.io.File

enum class ProjectStatus {
    CLEAR, FREE, LOADING, ERROR, DONE, CLOSE
}

data class ProjectEditorState(
    val projectStatus: ProjectStatus = ProjectStatus.FREE,
    val project: Project? = null,
    val editorCurrentContent: String? = null,
    val fileTreeNodeList: MutableList<TreeNode<File>> = mutableListOf()
) : UiState()

sealed class ProjectEditorIntent : UiIntent() {
    data object Clear : ProjectEditorIntent()

    data class OpenProject(val projectPath: String?) : ProjectEditorIntent()

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
                    if (intent.projectPath.isNullOrBlank()) {
                        setState(
                            state.value.copy(
                                projectStatus = ProjectStatus.ERROR
                            )
                        )
                    } else {
                        setState(
                            state.value.copy(
                                projectStatus = ProjectStatus.DONE,
                                fileTreeNodeList = getFileTreeNode(intent.projectPath)
                            )
                        )
                    }
                }
            }

            ProjectEditorIntent.CloseProject -> {
                setState(
                    state.value.copy(
                        projectStatus = ProjectStatus.CLOSE
                    )
                )
            }
        }
    }

    private fun getFileTreeNode(rootPath: String): MutableList<TreeNode<File>> {
        val root = TreeNode(File(rootPath))
        val queue = ArrayDeque<TreeNode<File>>()
        queue.addLast(root)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val childLevel = node.level + 1
            val files = node.value.listFiles() ?: continue
            val sortedFiles = FileUtils.sort(files)
            for (file in sortedFiles) {
                if (file.isDirectory) {
                    val child = TreeNode(file, childLevel)
                    node.addChild(child)
                    queue.addLast(child)
                } else {
                    node.addChild(TreeNode(file, childLevel))
                }
            }
        }
        return mutableListOf(root)
    }
}