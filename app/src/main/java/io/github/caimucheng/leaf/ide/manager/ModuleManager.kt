package io.github.caimucheng.leaf.ide.manager

import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.model.Module
import io.github.caimucheng.leaf.ide.util.ExternalRootPath
import io.github.caimucheng.leaf.ide.util.LeafIDEModuleRootPath
import io.github.caimucheng.leaf.ide.util.LeafIDEProjectPath
import io.github.caimucheng.leaf.ide.util.LeafIDERootPath
import io.github.caimucheng.leaf.module.path.Paths
import leaf.nodejs.module.NodeJSModuleAPP
import leaf.nodejs.module.creator.NodeJSFragmentCreator

class ModuleManager {
    var modules: MutableList<Module> = mutableListOf()

    init {
        refreshModule()
    }

    fun refreshModule() {
        modules = mutableListOf()
        val context = AppContext.current
        val paths = Paths(
            externalRootPath = ExternalRootPath,
            leafIDEModuleRootPath = LeafIDEModuleRootPath,
            leafIDERootPath = LeafIDERootPath,
            leafIDEProjectPath = LeafIDEProjectPath
        )
        modules.add(
            Module(
                moduleAPP = NodeJSModuleAPP(context = context, paths = paths),
                fragmentCreator = NodeJSFragmentCreator
            )
        )
    }

    companion object {
        @Volatile
        private var instance: ModuleManager? = null

        fun getInstance(): ModuleManager {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        instance = ModuleManager()
                    }
                }
            }
            return instance!!
        }
    }
}