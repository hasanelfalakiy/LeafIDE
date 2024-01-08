package io.github.caimucheng.leaf.common.util

import java.io.File

object FileUtils {
    fun sort(files: Array<File>?): List<File> {
        if (files.isNullOrEmpty()) return listOf()
        val directoryList = mutableListOf<File>()
        val fileList = mutableListOf<File>()
        files.forEach {
            if (it.isFile) fileList.add(it)
            else directoryList.add(it)
        }
        directoryList.sortBy { item -> item.name.lowercase() }
        fileList.sortBy { item -> item.name.lowercase() }
        directoryList.addAll(fileList)
        return directoryList
    }
}