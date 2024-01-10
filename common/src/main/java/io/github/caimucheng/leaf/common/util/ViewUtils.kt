package io.github.caimucheng.leaf.common.util

import android.view.MenuItem
import android.view.View

object ViewUtils {
    fun enableMenuItem(vararg items: MenuItem?) {
        items.forEach {
            it?.isEnabled = true
        }
    }

    fun disableMenuItem(vararg items: MenuItem?) {
        items.forEach {
            it?.isEnabled = false
        }
    }

    fun visibilityView(vararg views: View?) {
        views.forEach {
            it?.visibility = View.VISIBLE
        }
    }

    fun goneView(vararg views: View?) {
        views.forEach {
            it?.visibility = View.GONE
        }
    }
}