package io.github.caimucheng.leaf.plugin

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import io.github.caimucheng.leaf.plugin.creator.FragmentCreator

abstract class PluginAPP {

    open fun onCreate(hostContext: Context, selfResources: Resources) {}

    open fun onInstall(activityContext: Context) {}

    open fun onUninstall(activityContext: Context) {}

    abstract fun getFragmentCreator(): FragmentCreator

    abstract fun getPluginName(): String

    abstract fun getPluginDescription(): String

    abstract fun getPluginAuthor(): String

    abstract fun getProjectCardIcon(): Drawable

    abstract fun getProjectCardSubscript(): String

    abstract fun getTemplateIcon(): Drawable

    abstract fun getTemplateTitle(): String

}