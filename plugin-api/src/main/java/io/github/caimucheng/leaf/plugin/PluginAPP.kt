package io.github.caimucheng.leaf.plugin

import android.content.Context
import android.content.res.Resources

abstract class PluginAPP(protected val context: Context) {

    protected inline val resources: Resources
        get() {
            return context.resources
        }

    abstract fun onCreate()

    abstract fun getPluginName(): String

    abstract fun getPluginDescription(): String

    abstract fun getPluginAuthor(): String

}