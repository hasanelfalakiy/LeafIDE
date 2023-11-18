package io.github.caimucheng.leaf.plugin

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources

class PluginContext(base: Context?, private val resources: Resources) : ContextWrapper(base) {

    override fun getResources(): Resources {
        return resources
    }

}