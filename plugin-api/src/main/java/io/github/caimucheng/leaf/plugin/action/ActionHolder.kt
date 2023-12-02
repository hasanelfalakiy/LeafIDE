package io.github.caimucheng.leaf.plugin.action

import io.github.caimucheng.leaf.plugin.fragment.PluginFragment

data class ActionHolder(
    private val onPopBackStack: () -> Unit,
    private val onPopBackHome: (Boolean) -> Unit,
    private val onStartFragment: (PluginFragment) -> Unit,
    private val onReplaceFragment: (PluginFragment) -> Unit
) {

    fun popBackStack() = onPopBackStack()

    fun popBackHome(refreshProject: Boolean = false) = onPopBackHome(refreshProject)

    fun startFragment(pluginFragment: PluginFragment) = onStartFragment(pluginFragment)

    fun replaceFragment(pluginFragment: PluginFragment) = onReplaceFragment(pluginFragment)

}