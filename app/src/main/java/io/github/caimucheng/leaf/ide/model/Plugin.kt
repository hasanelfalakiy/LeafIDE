package io.github.caimucheng.leaf.ide.model

import android.graphics.drawable.Drawable
import androidx.core.content.edit
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.depository.AppDepository.Companion.PLUGIN_MIN_VERSION
import io.github.caimucheng.leaf.ide.util.pluginSharedPreferences
import io.github.caimucheng.leaf.plugin.PluginAPP

data class Plugin(
    val icon: Drawable,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val entrance: String,
    val pluginMinVersion: Int,
    val pluginAPP: PluginAPP,
    val pluginClassLoader: ClassLoader
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

inline val Plugin.isSupported: Boolean
    get() {
        return pluginMinVersion >= PLUGIN_MIN_VERSION
    }

inline val Plugin.isEnabled: Boolean
    get() {
        val pluginSharedPreferences = AppContext.current.pluginSharedPreferences
        return pluginSharedPreferences.getBoolean("${packageName}_isEnabled", true)
    }

fun Plugin.enable() {
    val pluginSharedPreferences = AppContext.current.pluginSharedPreferences
    pluginSharedPreferences.edit {
        putBoolean("${packageName}_isEnabled", true)
    }
}

fun Plugin.disable() {
    val pluginSharedPreferences = AppContext.current.pluginSharedPreferences
    pluginSharedPreferences.edit {
        putBoolean("${packageName}_isEnabled", false)
    }
}

fun Plugin.toggle() {
    val pluginSharedPreferences = AppContext.current.pluginSharedPreferences
    val isEnabled = pluginSharedPreferences.getBoolean("${packageName}_isEnabled", true)
    pluginSharedPreferences.edit {
        putBoolean("${packageName}_isEnabled", !isEnabled)
    }
}