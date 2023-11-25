package io.github.caimucheng.leaf.common.depository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileSelectorDepository {

    suspend fun enter(targetDirectory: File, matchingSuffix: List<String>?): List<File> {
        return withContext(Dispatchers.IO) {
            val files = (targetDirectory.listFiles()?.toList() ?: emptyList())
            if (matchingSuffix != null) {
                sortList(files.filter { if (it.isFile) ".${it.extension}" in matchingSuffix else true })
            } else {
                sortList(files)
            }
        }
    }

    suspend fun refresh(currentDirectory: File, matchingSuffix: List<String>?): List<File> {
        return withContext(Dispatchers.IO) {
            val files = (currentDirectory.listFiles()?.toList() ?: emptyList())
            if (matchingSuffix != null) {
                sortList(files.filter { if (it.isFile) ".${it.extension}" in matchingSuffix else true })
            } else {
                sortList(files)
            }
        }
    }

    private fun sortList(files: List<File>): List<File> {
        return files.sortedWith { first, second ->
            if (first.isDirectory && second.isFile) {
                return@sortedWith -1
            }
            if (first.isFile && second.isDirectory) {
                return@sortedWith 1
            }
            first.name.compareTo(second.name)
        }
    }

}