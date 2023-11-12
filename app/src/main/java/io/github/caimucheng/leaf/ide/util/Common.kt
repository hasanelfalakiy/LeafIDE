package io.github.caimucheng.leaf.ide.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import io.github.caimucheng.leaf.ide.application.AppContext

const val LAUNCH_MODE_KEY = "launch_mode"

inline val AppContext.launchModeSharedPreferences: SharedPreferences
    get() {
        return getSharedPreferences(LAUNCH_MODE_KEY, Context.MODE_PRIVATE)
    }

inline val AppContext.language: String
    get() {
        return ContextCompat.getString(this, io.github.caimucheng.leaf.common.R.string.language)
    }