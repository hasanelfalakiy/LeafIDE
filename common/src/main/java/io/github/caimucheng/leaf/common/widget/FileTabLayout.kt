package io.github.caimucheng.leaf.common.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.tabs.TabLayout
import java.io.File

class FileTabLayout : TabLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    fun addFile(file: File?) {
        file?.let {
            val tab = Tab()
            tab.setText(file.name)
            addTab(tab)
        }
    }
}