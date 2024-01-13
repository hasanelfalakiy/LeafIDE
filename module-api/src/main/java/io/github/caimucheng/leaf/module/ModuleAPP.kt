package io.github.caimucheng.leaf.module

import android.content.Context
import android.graphics.drawable.Drawable
import io.github.caimucheng.leaf.module.path.Paths

abstract class ModuleAPP(val context: Context, val paths: Paths) {

    abstract val moduleSupport: String

    abstract val versionName: String

    abstract val versionCode: Long

    abstract fun getName(): String

    abstract fun getIcon(): Drawable

    abstract fun getDescription(): String

    abstract fun getProjectCardIcon(): Drawable

    abstract fun getProjectCardSubscript(): String

    abstract fun getTemplateIcon(): Drawable

    abstract fun getTemplateTitle(): String

}