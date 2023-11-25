package io.github.caimucheng.leaf.common.viewmodel

import android.os.Environment
import androidx.lifecycle.viewModelScope
import io.github.caimucheng.leaf.common.depository.FileSelectorDepository
import io.github.caimucheng.leaf.common.mvi.MVIViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import kotlinx.coroutines.launch
import java.io.File

enum class FileState {
    Loading, Done
}

data class FileSelectorState(
    val currentDirectory: File = Environment.getExternalStorageDirectory(),
    val fileState: FileState = FileState.Done,
    val files: List<File> = emptyList()
) : UiState()

sealed class FileSelectorIntent : UiIntent() {
    data class Refresh(val matchingSuffix: List<String>?) : FileSelectorIntent()
    data class Enter(val targetDirectory: File, val matchingSuffix: List<String>?) : FileSelectorIntent()
}

class FileSelectorViewModel : MVIViewModel<FileSelectorState, FileSelectorIntent>() {

    private val fileSelectorDepository = FileSelectorDepository()

    override fun initialValue(): FileSelectorState {
        return FileSelectorState()
    }

    override fun handleIntent(intent: FileSelectorIntent, currentState: FileSelectorState) {
        when (intent) {
            is FileSelectorIntent.Refresh -> refresh(intent.matchingSuffix)
            is FileSelectorIntent.Enter -> enter(intent.targetDirectory, intent.matchingSuffix)
        }
    }

    private fun enter(targetDirectory: File, matchingSuffix: List<String>?) {
        viewModelScope.launch {
            setState(state.value.copy(fileState = FileState.Loading))
            val files = fileSelectorDepository.enter(targetDirectory, matchingSuffix)
            setState(
                state.value.copy(
                    fileState = FileState.Done,
                    files = files,
                    currentDirectory = targetDirectory
                )
            )
        }
    }

    private fun refresh(matchingSuffix: List<String>?) {
        viewModelScope.launch {
            setState(state.value.copy(fileState = FileState.Loading))
            val files = fileSelectorDepository.refresh(state.value.currentDirectory, matchingSuffix)
            setState(state.value.copy(fileState = FileState.Done, files = files))
        }
    }

}