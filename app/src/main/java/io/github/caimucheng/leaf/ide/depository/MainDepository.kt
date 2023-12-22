package io.github.caimucheng.leaf.ide.depository

import io.github.caimucheng.leaf.ide.util.LeafIDEPluginRootPath
import io.github.caimucheng.leaf.ide.util.LeafIDEProjectPath
import io.github.caimucheng.leaf.ide.util.LeafIDERootPath
import java.io.File

class MainDepository {

    fun initialize() {
        createPluginRoot()
        createLeafIDERoot()
        createLeafIDEProject()
    }

    private fun createPluginRoot() {
        LeafIDEPluginRootPath.mkdirs()
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