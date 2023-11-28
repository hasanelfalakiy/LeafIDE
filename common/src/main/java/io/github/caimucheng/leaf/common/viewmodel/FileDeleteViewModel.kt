package io.github.caimucheng.leaf.common.viewmodel

import androidx.lifecycle.viewModelScope
import io.github.caimucheng.leaf.common.mvi.MVIViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException


enum class FileDeleteTotalState {
    UNSTARTED, PROCESSING, DONE, FAILED
}

data class FileDeleteState(
    val name: String = "",
    val path: String = "",
    val totalState: FileDeleteTotalState = FileDeleteTotalState.UNSTARTED
) : UiState()

sealed class FileDeleteIntent : UiIntent() {

    data class Start(val name: String, val path: String) : FileDeleteIntent()

}

class FileDeleteViewModel : MVIViewModel<FileDeleteState, FileDeleteIntent>() {

    override fun initialValue(): FileDeleteState {
        return FileDeleteState()
    }

    override fun handleIntent(intent: FileDeleteIntent, currentState: FileDeleteState) {
        when (intent) {
            is FileDeleteIntent.Start -> start(intent.name, intent.path)
        }
    }

    private fun start(name: String, path: String) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    name = name,
                    path = path,
                    totalState = FileDeleteTotalState.PROCESSING
                )
            )
            try {
                startSuspend(path)
                setState(
                    state.value.copy(
                        totalState = FileDeleteTotalState.DONE
                    )
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                setState(state.value.copy(totalState = FileDeleteTotalState.FAILED))
            }
        }
    }

    private suspend fun startSuspend(path: String) {
        return withContext(Dispatchers.IO) {
            val file = File(path)
            val result = if (file.isFile) {
                processFile(file)
            } else {
                processFolder(file)
            }
            if (!result) {
                throw IOException()
            }
        }
    }

    private fun processFolder(file: File): Boolean {
        var result = true
        val children = file.listFiles() ?: emptyArray()
        for (child in children) {
            result = if (child.isFile) {
                processFile(child) && result
            } else {
                processFolder(child) && result
            }
        }
        result = file.delete() && result
        return result
    }

    private fun processFile(file: File): Boolean {
        return file.delete()
    }

}