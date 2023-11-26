package io.github.caimucheng.leaf.plugin

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentManager
import io.github.caimucheng.leaf.plugin.creator.FragmentCreator

abstract class PluginAPP {

    open fun onCreate(hostApplicationContext: Context, selfResources: Resources) {}

    open suspend fun onInstall(activityContext: Context, fragmentManager: FragmentManager) {}

    open suspend fun onUninstall(activityContext: Context, fragmentManager: FragmentManager) {}

    abstract fun getFragmentCreator(): FragmentCreator

    abstract fun getPluginName(): String

    abstract fun getPluginDescription(): String

    abstract fun getPluginAuthor(): String

    abstract fun getProjectCardIcon(): Drawable

    abstract fun getProjectCardSubscript(): String

    abstract fun getTemplateIcon(): Drawable

    abstract fun getTemplateTitle(): String

}