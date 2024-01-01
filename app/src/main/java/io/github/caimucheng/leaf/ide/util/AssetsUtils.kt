package io.github.caimucheng.leaf.ide.util

import android.content.Context

fun Context.getTextFromAssets(filePath: String): String {
    return assets.open(filePath).bufferedReader().use {
        it.readText()
    }
}