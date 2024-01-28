package io.github.caimucheng.leaf.ide.manager

import io.github.caimucheng.leaf.ide.model.Module
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.model.moduleSupport
import io.github.caimucheng.leaf.ide.util.LeafIDEProjectPath
import org.json.JSONObject
import java.io.File

object ProjectManager {
    private const val MODULE_SUPPORT_KEY = "moduleSupport"
    private const val DESCRIPTION_KEY = "description"
    private const val NAME_KEY = "name"

    // All legal projects
    private var projects: MutableList<Project> = mutableListOf()

    init {
        refreshProjects()
    }

    fun filterProject(modules: List<Module>, refresh: Boolean = true): List<Project> {
        if (refresh) refreshProjects()
        val list = mutableListOf<Project>()
        projects.forEach { project ->
            val module = modules.find { it.moduleSupport == project.moduleSupport }
            if (module != null) list.add(project)
        }
        return list.toList()
    }

    fun refreshProjects() {
        projects = mutableListOf()
        val children = LeafIDEProjectPath.listFiles() ?: emptyArray()
        for (child in children) {
            runCatching {
                if (child.isFile) return@runCatching
                val configurationDir = File(child, ".LeafIDE")
                if (configurationDir.isFile || !configurationDir.exists()) return@runCatching
                val workspaceFile = File(configurationDir, "workspace.json")
                if (!workspaceFile.exists() || workspaceFile.isDirectory) return@runCatching
                val workspace = JSONObject(workspaceFile.bufferedReader().use { it.readText() })
                val projectName = workspace.optString(NAME_KEY)
                val projectDescription = workspace.optString(DESCRIPTION_KEY)
                val moduleSupport = workspace.optString(MODULE_SUPPORT_KEY)
                val modules = ModuleManager.modules
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
}