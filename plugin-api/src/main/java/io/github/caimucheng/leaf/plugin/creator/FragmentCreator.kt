package io.github.caimucheng.leaf.plugin.creator

import io.github.caimucheng.leaf.plugin.fragment.PluginFragment

interface FragmentCreator {

    fun onNewProject(): PluginFragment

}