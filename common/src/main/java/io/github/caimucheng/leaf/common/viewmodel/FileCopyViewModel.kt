package io.github.caimucheng.leaf.common.viewmodel

import android.content.res.AssetManager
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
    val name: String = "",
    val from: String = "",
    val to: String = "",
    val progress: Int = 0,
    val totalState: FileCopyTotalState = FileCopyTotalState.UNSTARTED,
    val exception: Exception? = null
) : UiState()

sealed class FileCopyIntent : UiIntent() {

    data class Start(val name: String, val from: String, val to: String) : FileCopyIntent()

    data class StartFromAssets(
        val assets: AssetManager,
        val name: String,
        val from: String,
        val to: String,
    ) : FileCopyIntent()

}

class FileCopyViewModel : MVIViewModel<FileCopyState, FileCopyIntent>() {

    override fun initialValue(): FileCopyState {
        return FileCopyState()
    }

    override fun handleIntent(intent: FileCopyIntent, currentState: FileCopyState) {
        when (intent) {
            is FileCopyIntent.Start -> start(intent.name, intent.from, intent.to)
            is FileCopyIntent.StartFromAssets -> startFromAssets(
                intent.assets,
                intent.name,
                intent.from,
                intent.to
            )
        }
    }

    private fun startFromAssets(assets: AssetManager, name: String, from: String, to: String) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    name = name,
                    from = from,
                    to = to,
                    totalState = FileCopyTotalState.PROCESSING
                )
            )
            try {
                startFromAssetsSuspend(assets, from, to)
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

    private suspend fun startFromAssetsSuspend(assets: AssetManager, from: String, to: String) {
        return withContext(Dispatchers.IO) {
            processFile(assets, from, File(to))
        }
    }

    private fun start(name: String, from: String, to: String) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    name = name,
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
                val folderSize = statisticsFolderSize(fromFile)
                processFolder(
                    fromFile,
                    toFile,
                    ByteArray(8192),
                    folderSize,
                    0L
                )
            }
        }
    }

    private fun statisticsFolderSize(file: File): Long {
        var folderSize = 0L
        val children = file.listFiles() ?: emptyArray()
        for (child in children) {
            folderSize += if (child.isDirectory) {
                statisticsFolderSize(child)
            } else {
                child.length()
            }
        }
        return folderSize
    }

    private suspend fun processFolder(
        fromFile: File,
        toFile: File,
        buffer: ByteArray,
        folderSize: Long,
        transferred: Long
    ): Long {
        toFile.mkdirs()
        var currentTransferred = transferred

        val children = fromFile.listFiles() ?: emptyArray()
        for (child in children) {
            if (child.isDirectory) {
                currentTransferred = processFolder(
                    child,
                    File(toFile, child.name),
                    buffer,
                    folderSize,
                    currentTransferred
                )
            } else {
                val input = child.inputStream().buffered()
                val output = File(toFile, child.name).outputStream().buffered()
                input.use { _ ->
                    output.use { _ ->
                        var nRead: Int
                        var tempProgress = 0
                        var progress: Int
                        while (input.read(buffer, 0, buffer.size).also { nRead = it } >= 0) {
                            output.write(buffer, 0, nRead)
                            output.flush()
                            currentTransferred += nRead.toLong()
                            progress = (currentTransferred * 100L / folderSize).toInt()
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

        return currentTransferred
    }

    private suspend fun processFile(fromFile: File, toFile: File) {
        val input = fromFile.inputStream().buffered()
        val output = toFile.outputStream().buffered()
        input.use { _ ->
            output.use { _ ->
                val fileSize = fromFile.length()
                val buffer = ByteArray(8192)
                var nRead: Int
                var transferred: Long = 0
                var tempProgress = 0
                var progress: Int
                while (input.read(buffer, 0, buffer.size).also { nRead = it } >= 0) {
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

    private suspend fun processFile(assets: AssetManager, from: String, toFile: File) {
        val output = toFile.outputStream().buffered()
        val buffer = ByteArray(8192)
        var fileSize: Long = 0
        assets.open(from).buffered().use {
            var nRead: Int
            while (it.read(buffer, 0, buffer.size).also { nRead = it } >= 0) {
                fileSize += nRead
            }
        }
        assets.open(from).buffered().use { input ->
            output.use { _ ->
                var nRead: Int
                var transferred: Long = 0
                var tempProgress = 0
                var progress: Int
                while (input.read(buffer, 0, buffer.size).also { nRead = it } >= 0) {
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