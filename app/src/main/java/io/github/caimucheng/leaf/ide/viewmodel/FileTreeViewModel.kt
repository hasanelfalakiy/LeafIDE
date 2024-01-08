package io.github.caimucheng.leaf.ide.viewmodel

import io.github.caimucheng.leaf.common.mvi.MVIAppViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.common.util.FileUtils
import io.github.caimucheng.leaf.ide.model.TreeNode
import kotlinx.coroutines.launch
import java.io.File

enum class FileTreeAction {
    Free, Done
}

data class FileTreeState(
    val action: FileTreeAction = FileTreeAction.Free,
    val list: MutableList<TreeNode<File>> = mutableListOf()
) : UiState()

sealed class FileTreeIntent : UiIntent() {
    data class Open(val path: String) : FileTreeIntent()
}

object FileTreeViewModel : MVIAppViewModel<FileTreeState, FileTreeIntent>() {
    override fun initialValue(): FileTreeState {
        return FileTreeState()
    }

    override fun handleIntent(intent: FileTreeIntent, currentState: FileTreeState) {
        when (intent) {
            is FileTreeIntent.Open -> openProject(intent.path)
        }
    }

    private fun openProject(projectPath: String) {
        viewModelScope.launch {
            val root = TreeNode(File(projectPath))
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
            setState(
                state.value.copy(
                    action = FileTreeAction.Done,
                    list = mutableListOf(root)
                )
            )
        }
    }
}