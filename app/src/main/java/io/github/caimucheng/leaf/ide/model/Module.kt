package io.github.caimucheng.leaf.ide.model

import android.graphics.drawable.Drawable
import androidx.core.content.edit
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.util.moduleSharedPreferences
import io.github.caimucheng.leaf.module.ModuleAPP
import io.github.caimucheng.leaf.module.creator.FragmentCreator

data class Module(
    val moduleAPP: ModuleAPP,
    val fragmentCreator: FragmentCreator
)

inline val Module.moduleSupport: String
    get() {
        return moduleAPP.moduleSupport
    }

inline val Module.versionName: String
    get() {
        return moduleAPP.versionName
    }

inline val Module.versionCode: Long
    get() {
        return moduleAPP.versionCode
    }

inline val Module.icon: Drawable
    get() {
        return moduleAPP.getIcon()
    }

inline val Module.name: String
    get() {
        return moduleAPP.getName()
    }

inline val Module.description: String
    get() {
        return moduleAPP.getDescription()
    }

inline val Module.author: String
    get() {
        return moduleAPP.getAuthor()
    }

inline val Module.isEnabled: Boolean
    get() {
        val moduleSharedPreferences = AppContext.current.moduleSharedPreferences
        return moduleSharedPreferences.getBoolean("${moduleSupport}_isEnabled", true)
    }

fun Module.enable() {
    val moduleSharedPreferences = AppContext.current.moduleSharedPreferences
    moduleSharedPreferences.edit {
        putBoolean("${moduleSupport}_isEnabled", true)
    }
}

fun Module.disable() {
    val moduleSharedPreferences = AppContext.current.moduleSharedPreferences
    moduleSharedPreferences.edit {
        putBoolean("${moduleSupport}_isEnabled", false)
    }
}

fun Module.toggle() {
    val moduleSharedPreferences = AppContext.current.moduleSharedPreferences
    val isEnabled = moduleSharedPreferences.getBoolean("${moduleSupport}_isEnabled", true)
    moduleSharedPreferences.edit {
        putBoolean("${moduleSupport}_isEnabled", !isEnabled)
    }
}