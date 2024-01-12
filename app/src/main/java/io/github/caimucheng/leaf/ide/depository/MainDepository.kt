package io.github.caimucheng.leaf.ide.depository

import io.github.caimucheng.leaf.ide.util.LeafIDEModuleRootPath
import io.github.caimucheng.leaf.ide.util.LeafIDEProjectPath
import io.github.caimucheng.leaf.ide.util.LeafIDERootPath
import java.io.File

class MainDepository {

    fun initialize() {
        createModuleRoot()
        createLeafIDERoot()
        createLeafIDEProject()
    }

    private fun createModuleRoot() {
        LeafIDEModuleRootPath.mkdirs()
    }

    private fun createLeafIDERoot() {
        LeafIDEProjectPath.mkdirs()
    }

    private fun createLeafIDEProject() {
        LeafIDEProjectPath.mkdirs()

        runCatching {
            File(LeafIDERootPath, ".nomedia")
                .createNewFile()
        }
    }

}