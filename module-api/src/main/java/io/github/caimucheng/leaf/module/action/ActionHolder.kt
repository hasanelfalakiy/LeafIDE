package io.github.caimucheng.leaf.module.action

import io.github.caimucheng.leaf.module.fragment.ModuleFragment

data class ActionHolder(
    private val onPopBackStack: () -> Unit,
    private val onPopBackHome: (Boolean) -> Unit,
    private val onStartFragment: (ModuleFragment) -> Unit,
    private val onReplaceFragment: (ModuleFragment) -> Unit
) {

    fun popBackStack() = onPopBackStack()

    fun popBackHome(refreshProject: Boolean = false) = onPopBackHome(refreshProject)

    fun startFragment(moduleFragment: ModuleFragment) = onStartFragment(moduleFragment)

    fun replaceFragment(moduleFragment: ModuleFragment) = onReplaceFragment(moduleFragment)

}