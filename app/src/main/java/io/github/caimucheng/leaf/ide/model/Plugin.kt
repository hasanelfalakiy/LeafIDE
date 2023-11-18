package io.github.caimucheng.leaf.ide.model

import android.content.res.Resources
import android.graphics.drawable.Drawable
import io.github.caimucheng.leaf.plugin.PluginAPP

data class Plugin(
    val icon: Drawable,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val entrance: String,
    val pluginMinVersion: Int,
    val pluginAPP: PluginAPP
)

inline val Plugin.name: String
    get() {
        return pluginAPP.getPluginName()
    }

inline val Plugin.description: String
    get() {
        return pluginAPP.getPluginDescription()
    }

inline val Plugin.author: String
    get() {
        return pluginAPP.getPluginAuthor()
    }