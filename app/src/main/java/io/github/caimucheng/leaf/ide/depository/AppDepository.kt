package io.github.caimucheng.leaf.ide.depository

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import dalvik.system.PathClassLoader
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.model.Plugin
import io.github.caimucheng.leaf.plugin.PluginAPP
import io.github.caimucheng.leaf.plugin.PluginContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext

class AppDepository {

    companion object {

        const val PLUGIN_MIN_VERSION = 1

    }

    suspend fun refreshProjects(): List<String> {
        return withContext(Dispatchers.IO) {
            delay(2000L)
            listOf()
        }
    }

    suspend fun refreshPlugins(): List<Plugin> {
        return withContext(Dispatchers.IO) {
            val plugins: MutableList<Plugin> = ArrayList()
            val context = AppContext.current
            val packageManager = context.packageManager
            val applications =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (application in applications) {
                val icon = application.loadIcon(packageManager)
                val packageName = application.packageName
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                val versionName = packageInfo.versionName
                val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toLong()
                }
                val metaData = application.metaData ?: continue
                if (!metaData.getBoolean("leafide_plugin", false)) {
                    continue
                }
                val entrance = application.name ?: continue
                val pluginMinVersion = metaData.getInt("plugin_min_version", PLUGIN_MIN_VERSION)
                val resources = packageManager.getResourcesForApplication(application)
                val pluginContext = context.createPackageContext(
                    packageName,
                    Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
                )
                val classLoader = PathClassLoader(
                    pluginContext.packageResourcePath,
                    context.classLoader
                )

                supervisorScope {
                    val pluginAPPClass = classLoader.loadClass(entrance)
                    val isChild = PluginAPP::class.java.isAssignableFrom(pluginAPPClass)
                    if (!isChild) {
                        return@supervisorScope
                    }

                    // Get pluginAPP
                    val pluginAPP = pluginAPPClass.getConstructor(Context::class.java)
                        .newInstance(PluginContext(context, resources)) as PluginAPP

                    plugins.add(
                        Plugin(
                            icon = icon,
                            packageName = packageName,
                            versionName = versionName,
                            versionCode = versionCode,
                            entrance = entrance,
                            pluginMinVersion = pluginMinVersion,
                            pluginAPP = pluginAPP
                        )
                    )
                }
            }

            plugins.toList()
        }
    }

}