package io.github.caimucheng.leaf.common.viewmodel

import android.content.res.AssetManager
import android.os.Environment
import android.system.Os
import android.util.Log
import androidx.lifecycle.viewModelScope
import io.github.caimucheng.leaf.common.mvi.MVIViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.compress.archivers.ArchiveStreamFactory
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.File
import java.io.FileInputStream

enum class FileUnZipTotalState {
    UNSTARTED, PROCESSING, DONE, FAILED
}

data class FileUnZipState(
    val name: String = "",
    val from: String = "",
    val to: String = "",
    val progress: Int = 0,
    val totalState: FileUnZipTotalState = FileUnZipTotalState.UNSTARTED,
    val exception: Exception? = null
) : UiState()

sealed class FileUnZipIntent : UiIntent() {

    data class Start(val name: String, val from: String, val to: String, val type: String) :
        FileUnZipIntent()

    data class StartFromAssets(
        val assets: AssetManager,
        val name: String,
        val from: String,
        val to: String,
        val type: String,
    ) : FileUnZipIntent()

}

class FileUnZipViewModel : MVIViewModel<FileUnZipState, FileUnZipIntent>() {

    override fun initialValue(): FileUnZipState {
        return FileUnZipState()
    }

    override fun handleIntent(intent: FileUnZipIntent, currentState: FileUnZipState) {
        when (intent) {
            is FileUnZipIntent.Start -> start(intent.name, intent.from, intent.to, intent.type)
            is FileUnZipIntent.StartFromAssets -> startFromInputStream(
                intent.assets,
                intent.name,
                intent.from,
                intent.to,
                intent.type
            )
        }
    }

    private fun startFromInputStream(
        assets: AssetManager,
        name: String,
        from: String,
        to: String,
        type: String
    ) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    name = name,
                    from = from,
                    to = to,
                    totalState = FileUnZipTotalState.PROCESSING
                )
            )
            try {
                startFromInputStreamSuspend(assets, from, to, type)
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

    private fun start(name: String, from: String, to: String, type: String) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    name = name,
                    from = from,
                    to = to,
                    totalState = FileUnZipTotalState.PROCESSING
                )
            )
            try {
                startSuspend(from, to, type)
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
        to: String,
        type: String
    ) {
        return withContext(Dispatchers.IO) {
            val toFile = File(to)
            when (type) {
                "zip" -> decompressZip(assets, from, toFile)
                "gz" -> decompressTarGz(assets, from, toFile)
                else -> throw IllegalArgumentException("Unknown type")
            }
        }
    }

    private suspend fun startSuspend(from: String, to: String, type: String) {
        return withContext(Dispatchers.IO) {
            val fromFile = File(from)
            val toFile = File(to)
            when (type) {
                "zip" -> decompressZip(fromFile, toFile)
                "gz" -> decompressTarGz(fromFile, toFile)
                else -> throw IllegalArgumentException("Unknown type")
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun decompressTarGz(fromFile: File, toFile: File) {
        toFile.mkdirs()

        val buffer = ByteArray(8192)
        val archiveStreamFactory = ArchiveStreamFactory("UTF-8")
        var transferred: Long = 0
        var decompressedSize: Long = 0
        FileInputStream(fromFile).buffered().use {
            val archiveInputStream =
                archiveStreamFactory.createArchiveInputStream<TarArchiveInputStream>(
                    ArchiveStreamFactory.TAR,
                    GzipCompressorInputStream(it)
                )
            var archiveEntry: TarArchiveEntry
            while (archiveInputStream.nextEntry.also { entry -> archiveEntry = entry } != null) {
                decompressedSize += archiveEntry.size
            }
        }
        FileInputStream(fromFile).use {
            val archiveInputStream =
                archiveStreamFactory.createArchiveInputStream<TarArchiveInputStream>(
                    ArchiveStreamFactory.TAR,
                    GzipCompressorInputStream(it)
                )
            var archiveEntry: TarArchiveEntry
            while (archiveInputStream.nextEntry.also { entry -> archiveEntry = entry } != null) {
                val outputFile =
                    File(toFile, archiveEntry.name)

                if (archiveEntry.isSymbolicLink) {
                    try {
                        Os.symlink(
                            relativeTo(
                                outputFile.parentFile ?: outputFile,
                                archiveEntry.linkName
                            ).absolutePath,
                            outputFile.absolutePath
                        )
                    } catch (_: Exception) {
                    }
                    continue
                }

                if (archiveEntry.isDirectory) {
                    outputFile.mkdirs()
                    continue
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

                val splitIndex = archiveEntry.name.lastIndexOf("bin/")
                if (splitIndex != -1) {
                    outputFile.setExecutable(true, true)
                }
            }
        }
    }

    private suspend fun decompressTarGz(assets: AssetManager, from: String, toFile: File) {
        toFile.mkdirs()

        val buffer = ByteArray(8192)
        val archiveStreamFactory = ArchiveStreamFactory("UTF-8")
        var transferred: Long = 0
        var decompressedSize: Long = 0
        assets.open(from).buffered().use {
            val archiveInputStream =
                archiveStreamFactory.createArchiveInputStream<TarArchiveInputStream>(
                    ArchiveStreamFactory.TAR,
                    GzipCompressorInputStream(it)
                )
            var archiveEntry: TarArchiveEntry
            while (archiveInputStream.nextEntry.also { entry -> archiveEntry = entry } != null) {
                decompressedSize += archiveEntry.size
            }
        }
        assets.open(from).use {
            val archiveInputStream =
                archiveStreamFactory.createArchiveInputStream<TarArchiveInputStream>(
                    ArchiveStreamFactory.TAR,
                    GzipCompressorInputStream(it)
                )
            var archiveEntry: TarArchiveEntry
            while (archiveInputStream.nextEntry.also { entry -> archiveEntry = entry } != null) {
                val outputFile =
                    File(toFile, archiveEntry.name)

                if (archiveEntry.isSymbolicLink) {
                    try {
                        Os.symlink(
                            relativeTo(
                                outputFile.parentFile ?: outputFile,
                                archiveEntry.linkName
                            ).absolutePath,
                            outputFile.absolutePath
                        )
                    } catch (_: Exception) {
                    }
                    continue
                }

                if (archiveEntry.isDirectory) {
                    outputFile.mkdirs()
                    continue
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

                val splitIndex = archiveEntry.name.lastIndexOf("bin/")
                if (splitIndex != -1) {
                    outputFile.setExecutable(true, true)
                }
            }
        }
    }

    private fun relativeTo(baseFile: File, name: String): File {
        var currentBaseFile = baseFile
        val paths = name.split("/")
        if (paths[0].isEmpty()) {
            return File(name)
        }
        for (path in paths) {
            currentBaseFile = if (path == "..") {
                currentBaseFile.parentFile ?: currentBaseFile
            } else {
                File(currentBaseFile, path)
            }
        }
        return currentBaseFile
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun decompressZip(fromFile: File, toFile: File) {
        toFile.mkdirs()

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

                if (archiveEntry.isDirectory) {
                    outputFile.mkdirs()
                    continue
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

    private suspend fun decompressZip(assets: AssetManager, from: String, toFile: File) {
        toFile.mkdirs()

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

                if (archiveEntry.isDirectory) {
                    outputFile.mkdirs()
                    continue
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