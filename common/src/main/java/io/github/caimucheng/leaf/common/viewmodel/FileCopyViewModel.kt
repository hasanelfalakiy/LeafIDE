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

enum class FileCopyTotalState {
    UNSTARTED, PROCESSING, DONE, FAILED
}

data class FileCopyState(
    val from: String = "",
    val to: String = "",
    val progress: Int = 0,
    val totalState: FileCopyTotalState = FileCopyTotalState.UNSTARTED,
    val exception: Exception? = null
) : UiState()

sealed class FileCopyIntent : UiIntent() {

    data class Start(val from: String, val to: String) : FileCopyIntent()

}

class FileCopyViewModel : MVIViewModel<FileCopyState, FileCopyIntent>() {

    override fun initialValue(): FileCopyState {
        return FileCopyState()
    }

    override fun handleIntent(intent: FileCopyIntent, currentState: FileCopyState) {
        when (intent) {
            is FileCopyIntent.Start -> start(intent.from, intent.to)
        }
    }

    private fun start(from: String, to: String) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    from = from,
                    to = to,
                    totalState = FileCopyTotalState.PROCESSING
                )
            )
            try {
                startSuspend(from, to)
                setState(
                    state.value.copy(
                        totalState = FileCopyTotalState.DONE
                    )
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                setState(state.value.copy(totalState = FileCopyTotalState.FAILED, exception = e))
            }
        }
    }

    private suspend fun startSuspend(from: String, to: String) {
        return withContext(Dispatchers.IO) {
            val fromFile = File(from)
            val toFile = File(to)
            if (fromFile.isFile) {
                processFile(fromFile, toFile)
            } else {
                processFolder(fromFile, toFile)
            }
        }
    }

    private fun processFolder(fromFile: File, toFile: File) {

    }

    private suspend fun processFile(fromFile: File, toFile: File) {
        val input = fromFile.inputStream().buffered()
        val output = toFile.outputStream().buffered()
        input.use { _ ->
            output.use { _ ->
                val fileSize = fromFile.length()
                var transferred: Long = 0
                val buffer = ByteArray(8192)
                var nRead: Int
                var tempProgress = 0
                var progress: Int
                while (input.read(buffer, 0, 8192).also { nRead = it } >= 0) {
                    output.write(buffer, 0, nRead)
                    output.flush()
                    transferred += nRead.toLong()
                    progress = (transferred * 100L / fileSize).toInt()
                    if (progress > tempProgress) {
                        tempProgress = progress
                        withContext(Dispatchers.Main) {
                            setState(state.value.copy(progress = progress))
                        }
                    }
                }
            }
        }
    }

}