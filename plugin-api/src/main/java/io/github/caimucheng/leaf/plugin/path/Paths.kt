package io.github.caimucheng.leaf.plugin.path

import java.io.File

data class Paths(
    val externalRootPath: File,
    val leafIDEPluginRootPath: File,
    val leafIDERootPath: File,
    val leafIDEProjectPath: File
)