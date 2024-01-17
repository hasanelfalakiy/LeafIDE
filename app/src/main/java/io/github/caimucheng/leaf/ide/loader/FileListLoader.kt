package io.github.caimucheng.leaf.ide.loader

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileListLoader {
    private val cacheFiles = mutableMapOf<String, MutableList<File>>()

    private fun getFileList(file: File): List<File> {
        return (file.listFiles() ?: emptyArray()).run {
            sortedWith { o1, o2 ->
                if (o1.isDirectory && o2.isFile) {
                    -1
                } else if (o1.isFile && o2.isDirectory) {
                    1
                } else {
                    o1.name.lowercase().compareTo(o2.name.lowercase())
                }
            }
        }
    }

    suspend fun loadFileList(path: String) = withContext(Dispatchers.IO) {
        val result = cacheFiles[path] ?: run {
            val files = getFileList(File(path))
            cacheFiles[path] = files.toMutableList()
            files.forEach {
                if (it.isDirectory) {
                    cacheFiles[it.absolutePath] = getFileList(it).toMutableList()
                }
            }
            files.toMutableList()
        }
        result
    }

    fun getCacheFileList(path: String) = cacheFiles[path] ?: emptyList()

    fun removeFileInCache(currentFile: File): Boolean {
        if (currentFile.isDirectory) {
            cacheFiles.remove(currentFile.absolutePath)
        }
        val parent = currentFile.parentFile
        val parentPath = parent?.absolutePath
        val parentFiles = cacheFiles[parentPath]
        return parentFiles?.remove(currentFile) ?: false
    }

    override fun toString(): String {
        return "FileListLoader(cacheFiles=$cacheFiles)"
    }
}