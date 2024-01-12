package io.github.caimucheng.leaf.module.path

import java.io.File

data class Paths(
    val externalRootPath: File,
    val leafIDEModuleRootPath: File,
    val leafIDERootPath: File,
    val leafIDEProjectPath: File
)