package io.github.caimucheng.leaf.ide.viewmodel

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentManager
import io.github.caimucheng.leaf.common.mvi.MVIAppViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.ide.depository.AppDepository
import io.github.caimucheng.leaf.ide.model.Plugin
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.model.isEnabled
import io.github.caimucheng.leaf.ide.model.isSupported
import io.github.caimucheng.leaf.ide.util.LeafIDEPluginRootPath
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

enum class PluginState {
    Loading, Done
}

enum class ProjectState {
    Loading, Done
}

data class AppState(
    val pluginState: PluginState = PluginState.Done,
    val plugins: List<Plugin> = emptyList(),
    val projectState: ProjectState = ProjectState.Done,
    val projects: List<Project> = emptyList(),
    val isRefreshed: Boolean = false
) : UiState()

sealed class AppIntent : UiIntent() {
    data object Refresh : AppIntent()

    data class Install(
        val packageName: String,
        val context: Context,
        val fragmentManager: FragmentManager
    ) : AppIntent()

    data class Uninstall(
        val packageName: String,
        val context: Context,
        val fragmentManager: FragmentManager
    ) : AppIntent()

}

object AppViewModel : MVIAppViewModel<AppState, AppIntent>() {

    private val appDepository: AppDepository = AppDepository()

    override fun initialValue(): AppState {
        return AppState()
    }

    override fun handleIntent(intent: AppIntent, currentState: AppState) {
        when (intent) {
            AppIntent.Refresh -> refresh()

            is AppIntent.Install -> install(
                intent.packageName,
                intent.context,
                intent.fragmentManager
            )

            is AppIntent.Uninstall -> uninstall(
                intent.packageName,
                intent.context,
                intent.fragmentManager
            )
        }
    }

    private fun uninstall(packageName: String, context: Context, fragmentManager: FragmentManager) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    pluginState = PluginState.Loading,
                    projectState = ProjectState.Loading
                )
            )

            val plugins = ArrayList(appDepository.refreshPlugins(state.value.plugins))
            val uninstalledPlugin = plugins.find { it.packageName == packageName }
            if (uninstalledPlugin != null) {
                val file = File(LeafIDEPluginRootPath, "${uninstalledPlugin.packageName}.apk")
                if (file.exists()) {
                    file.delete()
                }
                plugins.remove(uninstalledPlugin)
                try {
                    withContext(Dispatchers.Main) {
                        uninstalledPlugin.pluginAPP.onUninstall(context, fragmentManager)
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    e.printStackTrace()
                    Log.e("AppViewModel", "Install plugin failed: " + e.message)
                }
            }

            val projects =
                appDepository.refreshProjects(plugins.filter { it.isSupported && it.isEnabled })

            setState(
                state.value.copy(
                    pluginState = PluginState.Done,
                    plugins = plugins,
                    projectState = ProjectState.Done,
                    projects = projects,
                    isRefreshed = true
                )
            )
        }
    }

    private fun install(
        packageName: String,
        context: Context,
        fragmentManager: FragmentManager
    ) {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    pluginState = PluginState.Loading,
                    projectState = ProjectState.Loading
                )
            )
            val plugins = appDepository.refreshPlugins(state.value.plugins)
            val installedPlugin = plugins.find { it.packageName == packageName }
            if (installedPlugin != null) {
                try {
                    withContext(Dispatchers.Main) {
                        installedPlugin.pluginAPP.onInstall(context, fragmentManager)
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                    e.printStackTrace()
                    Log.e("AppViewModel", "Install plugin failed: " + e.message)
                }
            }

            val projects =
                appDepository.refreshProjects(plugins.filter { it.isSupported && it.isEnabled })

            setState(
                state.value.copy(
                    pluginState = PluginState.Done,
                    plugins = plugins,
                    projectState = ProjectState.Done,
                    projects = projects,
                    isRefreshed = true
                )
            )
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            setState(
                state.value.copy(
                    pluginState = PluginState.Loading,
                    projectState = ProjectState.Loading
                )
            )
            val plugins = appDepository.refreshPlugins(state.value.plugins)
            val projects =
                appDepository.refreshProjects(plugins.filter { it.isSupported && it.isEnabled })
            setState(
                state.value.copy(
                    pluginState = PluginState.Done,
                    plugins = plugins,
                    projectState = ProjectState.Done,
                    projects = projects,
                    isRefreshed = true
                )
            )
        }
    }

}