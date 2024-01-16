package io.github.caimucheng.leaf.ide.depository

import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.model.Module
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.model.moduleSupport
import io.github.caimucheng.leaf.ide.util.ExternalRootPath
import io.github.caimucheng.leaf.ide.util.LeafIDEModuleRootPath
import io.github.caimucheng.leaf.ide.util.LeafIDEProjectPath
import io.github.caimucheng.leaf.ide.util.LeafIDERootPath
import io.github.caimucheng.leaf.module.path.Paths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import leaf.nodejs.module.NodeJSModuleAPP
import leaf.nodejs.module.creator.NodeJSFragmentCreator
import org.json.JSONObject
import java.io.File

class AppDepository {

    suspend fun refreshProjects(modules: List<Module>): List<Project> {
        return withContext(Dispatchers.IO) {
            val projects: MutableList<Project> = ArrayList()
            supervisorScope {
                runCatching {
                    val children = LeafIDEProjectPath.listFiles() ?: emptyArray()
                    for (child in children) {
                        if (child.isFile) return@runCatching
                        val configurationDir = File(child, ".LeafIDE")
                        if (configurationDir.isFile || !configurationDir.exists()) return@runCatching
                        val workspaceFile = File(configurationDir, "workspace.json")
                        if (!workspaceFile.exists() || workspaceFile.isDirectory) return@runCatching
                        val workspace =
                            JSONObject(workspaceFile.bufferedReader().use { it.readText() })
                        val projectName = workspace.optString("name")
                        val projectDescription = workspace.optString("description")
                        val moduleSupport = workspace.optString("moduleSupport")
                        val module = modules.find {
                            it.moduleSupport == moduleSupport
                        } ?: return@runCatching
                        projects.add(
                            Project(
                                child.absolutePath,
                                projectName,
                                projectDescription,
                                module,
                                workspace
                            )
                        )
                    }
                }
            }
            projects.toList()
        }
    }

    suspend fun refreshModules(): List<Module> {
        return withContext(Dispatchers.IO) {
            val context = AppContext.current
            val paths = Paths(
                externalRootPath = ExternalRootPath,
                leafIDEModuleRootPath = LeafIDEModuleRootPath,
                leafIDERootPath = LeafIDERootPath,
                leafIDEProjectPath = LeafIDEProjectPath
            )

            listOf(
                Module(
                    moduleAPP = NodeJSModuleAPP(
                        context = context,
                        paths = paths
                    ),
                    fragmentCreator = NodeJSFragmentCreator
                )
            )
        }
    }

}