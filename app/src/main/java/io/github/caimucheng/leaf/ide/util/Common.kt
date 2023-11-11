package io.github.caimucheng.leaf.ide.util

import androidx.core.content.ContextCompat
import io.github.caimucheng.leaf.ide.application.AppContext

val AppContext.language: String
    get() {
        return ContextCompat.getString(this, io.github.caimucheng.leaf.common.R.string.language)
    }