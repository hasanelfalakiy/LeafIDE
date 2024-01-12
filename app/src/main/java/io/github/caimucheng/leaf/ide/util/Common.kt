package io.github.caimucheng.leaf.ide.util

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.ContextCompat
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.viewmodel.LaunchMode

const val LAUNCH_MODE_KEY = "launch_mode"

const val MODULE_KEY = "module"

inline val AppContext.launchModeSharedPreferences: SharedPreferences
    get() {
        return getSharedPreferences(LAUNCH_MODE_KEY, Context.MODE_PRIVATE)
    }

inline val AppContext.moduleSharedPreferences: SharedPreferences
    get() {
        return getSharedPreferences(MODULE_KEY, Context.MODE_PRIVATE)
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

fun openWebPage(context: Context, url: String) {
    val webPage = Uri.parse(url)
    val intent = Intent(Intent.ACTION_VIEW, webPage)
    context.startActivity(intent)
}

fun getVersionNameSelf(context: Context): String {
    val manager = context.packageManager
    val info = manager.getPackageInfo(context.packageName, 0)
    return info.versionName
}