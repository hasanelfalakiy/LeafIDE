package io.github.caimucheng.leaf.ide.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.viewmodel.LaunchMode

const val LAUNCH_MODE_KEY = "launch_mode"

const val PLUGIN_KEY = "plugin"

inline val AppContext.launchModeSharedPreferences: SharedPreferences
    get() {
        return getSharedPreferences(LAUNCH_MODE_KEY, Context.MODE_PRIVATE)
    }

inline val AppContext.pluginSharedPreferences: SharedPreferences
    get() {
        return getSharedPreferences(PLUGIN_KEY, Context.MODE_PRIVATE)
    }

inline val AppContext.isInitializedLaunchMode: Boolean
    get() {
        return launchModeSharedPreferences.getString("launchMode", null) != null
    }

inline val AppContext.launchMode: LaunchMode
    get() {
        val launchMode = launchModeSharedPreferences.getString("launchMode", null)
            ?: return LaunchMode.LaunchFromInternalStorage
        return LaunchMode.valueOf(launchMode)
    }

inline val AppContext.language: String
    get() {
        return ContextCompat.getString(this, io.github.caimucheng.leaf.common.R.string.language)
    }