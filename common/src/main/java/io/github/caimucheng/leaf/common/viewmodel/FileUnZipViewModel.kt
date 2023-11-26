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
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

enum class FileUnZipTotalState {
    UNSTARTED, PROCESSING, DONE, FAILED
}

data class FileUnZipState(
    val from: String = "",
    val to: String = "",
    val progress: Int = 0,
    val totalState: FileUnZipTotalState = FileUnZipTotalState.UNSTARTED,
    val exception: Exception? = null
) : UiState()

sealed class FileUnZipIntent : UiIntent() {

    data class Start(val from: String, val to: String) : FileUnZipIntent()

    data class StartFromAssets(
        val assets: AssetManager,
        val from: String,
        val to: String,
    ) : FileUnZipIntent()

}

class FileUnZipViewModel : MVIViewModel<FileUnZipState, FileUnZipIntent>() {

    override fun initialValue(): FileUnZipState {
        return FileUnZipState()
    }

    override fun handleIntent(intent: FileUnZipIntent, currentState: FileUnZipState) {
        when (intent) {
            is FileUnZipIntent.Start -> start(intent.from, intent.to)
            is FileUnZipIntent.StartFromAssets -> startFromInputStream(
                intent.assets,
                intent.to,
                intent.from
            )
        }
    }

    private fun startFromInputStream(
        assets: AssetManager,
        from: String,
        to: String
    ) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    from = from,
                    to = to,
                    totalState = FileUnZipTotalState.PROCESSING
                )
            )
            try {
                startFromInputStreamSuspend(assets, from, to)
                setState(
                    state.value.copy(
                        totalState = FileUnZipTotalState.DONE
                    )
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                setState(state.value.copy(totalState = FileUnZipTotalState.FAILED, exception = e))
            }
        }
    }

    private fun start(from: String, to: String) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    from = from,
                    to = to,
                    totalState = FileUnZipTotalState.PROCESSING
                )
            )
            try {
                startSuspend(from, to)
                setState(
                    state.value.copy(
                        totalState = FileUnZipTotalState.DONE
                    )
                )
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                setState(state.value.copy(totalState = FileUnZipTotalState.FAILED, exception = e))
            }
        }
    }

    private suspend fun startFromInputStreamSuspend(
        assets: AssetManager,
        from: String,
        to: String
    ) {
        return withContext(Dispatchers.IO) {
            decompress(assets, from, File(to))
        }
    }

    private suspend fun startSuspend(from: String, to: String) {
        return withContext(Dispatchers.IO) {
            val fromFile = File(from)
            val toFile = File(to)
            decompress(fromFile, toFile)
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun decompress(fromFile: File, toFile: File) {
        val buffer = ByteArray(8192)
        val archiveStreamFactory = ArchiveStreamFactory("UTF-8")
        var transferred: Long = 0
        var decompressedSize: Long = 0
        FileInputStream(fromFile).buffered().use {
            val archiveInputStream =
                archiveStreamFactory.createArchiveInputStream<ZipArchiveInputStream>(
                    ArchiveStreamFactory.ZIP,
                    it
                )
            var archiveEntry: ZipArchiveEntry
            while (archiveInputStream.nextEntry.also { entry -> archiveEntry = entry } != null) {
                decompressedSize += archiveEntry.size
            }
        }
        FileInputStream(fromFile).buffered().use {
            val archiveInputStream =
                archiveStreamFactory.createArchiveInputStream<ZipArchiveInputStream>(
                    ArchiveStreamFactory.ZIP,
                    it
                )
            var archiveEntry: ZipArchiveEntry
            while (archiveInputStream.nextEntry.also { entry -> archiveEntry = entry } != null) {
                val outputFile = File(toFile, archiveEntry.name)
                if (outputFile.parentFile?.exists() == false) {
                    outputFile.parentFile?.mkdirs()
                }

                val output = outputFile.outputStream().buffered()
                output.use {
                    var nRead: Int
                    var tempProgress = 0
                    var progress: Int
                    while (archiveInputStream.read(buffer, 0, buffer.size)
                            .also { bytes -> nRead = bytes } >= 0
                    ) {
                        output.write(buffer, 0, nRead)
                        output.flush()
                        transferred += nRead.toLong()
                        progress = (transferred * 100L / decompressedSize).toInt()
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

    private suspend fun decompress(assets: AssetManager, from: String, toFile: File) {
        val buffer = ByteArray(8192)
        val archiveStreamFactory = ArchiveStreamFactory("UTF-8")
        var transferred: Long = 0
        var decompressedSize: Long = 0
        assets.open(from).buffered().use {
            val archiveInputStream =
                archiveStreamFactory.createArchiveInputStream<ZipArchiveInputStream>(
                    ArchiveStreamFactory.ZIP,
                    it
                )
            var archiveEntry: ZipArchiveEntry
            while (archiveInputStream.nextEntry.also { entry -> archiveEntry = entry } != null) {
                decompressedSize += archiveEntry.size
            }
        }
        assets.open(from).use {
            val archiveInputStream =
                archiveStreamFactory.createArchiveInputStream<ZipArchiveInputStream>(
                    ArchiveStreamFactory.ZIP,
                    it
                )
            var archiveEntry: ZipArchiveEntry
            while (archiveInputStream.nextEntry.also { entry -> archiveEntry = entry } != null) {
                val outputFile = File(toFile, archiveEntry.name)
                if (outputFile.parentFile?.exists() == false) {
                    outputFile.parentFile?.mkdirs()
                }

                val output = outputFile.outputStream().buffered()
                output.use {
                    var nRead: Int
                    var tempProgress = 0
                    var progress: Int
                    while (archiveInputStream.read(buffer, 0, buffer.size)
                            .also { bytes -> nRead = bytes } >= 0
                    ) {
                        output.write(buffer, 0, nRead)
                        output.flush()
                        transferred += nRead.toLong()
                        progress = (transferred * 100L / decompressedSize).toInt()
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

}