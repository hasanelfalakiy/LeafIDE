package io.github.caimucheng.leaf.ide.depository

import android.content.pm.PackageManager
import android.os.Build
import dalvik.system.DexClassLoader
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.model.Plugin
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.util.ExternalRootPath
import io.github.caimucheng.leaf.ide.util.LeafIDEPluginRootPath
import io.github.caimucheng.leaf.ide.util.LeafIDEProjectPath
import io.github.caimucheng.leaf.ide.util.LeafIDERootPath
import io.github.caimucheng.leaf.plugin.PluginAPP
import io.github.caimucheng.leaf.plugin.path.Paths
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

class AppDepository {

    companion object {
        const val PLUGIN_MIN_VERSION = 1
    }

    suspend fun refreshProjects(plugins: List<Plugin>): List<Project> {
        return withContext(Dispatchers.IO) {
            val projects: MutableList<Project> = ArrayList()
            supervisorScope {
                val children = LeafIDEProjectPath.listFiles() ?: emptyArray()
                for (child in children) {
                    runCatching {
                        if (child.isFile) return@runCatching
                        val configurationDir = File(child, ".LeafIDE")
                        if (configurationDir.isFile || !configurationDir.exists()) return@runCatching
                        val workspaceFile = File(configurationDir, "workspace.json")
                        if (!workspaceFile.exists() || workspaceFile.isDirectory) return@runCatching
                        val workspace =
                            JSONObject(workspaceFile.bufferedReader().use { it.readText() })
                        val projectName = workspace.optString("name")
                        val projectDescription = workspace.optString("description")
                        val pluginSupport = workspace.optString("pluginSupport")
                        val plugin =
                            plugins.find { it.packageName == pluginSupport } ?: return@runCatching
                        projects.add(
                            Project(
                                child.absolutePath,
                                projectName,
                                projectDescription,
                                plugin,
                                workspace
                            )
                        )
                    }
                }
            }
            projects.toList()
        }
    }

    suspend fun refreshPlugins(loadedPlugins: List<Plugin>): List<Plugin> {
        return withContext(Dispatchers.IO) {
            val plugins: MutableList<Plugin> = ArrayList()
            val context = AppContext.current
            val children = LeafIDEPluginRootPath.listFiles() ?: emptyArray()
            for (child in children) {
                if (child.isDirectory || !child.name.endsWith(".apk")) {
                    continue
                }

                val loadedPlugin = loadedPlugins.find { plugin -> plugin.packageName == child.name }
                if (loadedPlugin != null) {
                    plugins.add(loadedPlugin)
                    continue
                }

                runCatching {
                    val packageInfo = context.packageManager.getPackageArchiveInfo(
                        child.absolutePath, PackageManager.GET_META_DATA
                    ) ?: return@runCatching
                    val application = packageInfo.applicationInfo
                    application.sourceDir = child.absolutePath
                    application.publicSourceDir = child.absolutePath

                    val icon = application.loadIcon(context.packageManager)
                    val packageName = application.packageName
                    val versionName = packageInfo.versionName
                    val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        packageInfo.longVersionCode
                    } else {
                        @Suppress("DEPRECATION")
                        packageInfo.versionCode.toLong()
                    }
                    val metaData = application.metaData ?: return@runCatching
                    if (!metaData.getBoolean("leafide_plugin", false)) {
                        return@runCatching
                    }
                    val entrance = application.name ?: return@runCatching
                    val pluginMinVersion = metaData.getInt("plugin_min_version", PLUGIN_MIN_VERSION)
                    val resources = context.packageManager.getResourcesForApplication(application)
                    val classLoader = DexClassLoader(
                        child.absolutePath,
                        context.cacheDir.absolutePath,
                        null,
                        context.classLoader
                    )

                    val pluginAPPClass = classLoader.loadClass(entrance)
                    val isChild = PluginAPP::class.java.isAssignableFrom(pluginAPPClass)
                    if (!isChild) {
                        return@runCatching
                    }

                    // Get pluginAPP
                    val paths = Paths(
                        ExternalRootPath,
                        LeafIDEPluginRootPath,
                        LeafIDERootPath,
                        LeafIDEProjectPath
                    )
                    val pluginAPP = pluginAPPClass.getConstructor().newInstance() as PluginAPP
                    pluginAPP.onCreate(context, resources, paths)

                    plugins.add(
                        Plugin(
                            icon = icon,
                            packageName = packageName,
                            versionName = versionName,
                            versionCode = versionCode,
                            entrance = entrance,
                            pluginMinVersion = pluginMinVersion,
                            pluginAPP = pluginAPP,
                            pluginClassLoader = classLoader
                        )
                    )
                }.exceptionOrNull()?.printStackTrace()
            }

            plugins.toList()
        }
    }

}