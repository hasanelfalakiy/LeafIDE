package io.github.caimucheng.leaf.module.creator

import io.github.caimucheng.leaf.module.fragment.ModuleFragment

interface FragmentCreator {

    fun onNewProject(): ModuleFragment

    fun onManageModule(): ModuleFragment

}