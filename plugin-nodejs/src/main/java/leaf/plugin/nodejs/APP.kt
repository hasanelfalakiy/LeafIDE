package leaf.plugin.nodejs

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import io.github.caimucheng.leaf.plugin.PluginAPP

class APP(context: Context) : PluginAPP(context) {

    override fun onCreate() {

    }

    override fun getPluginName(): String {
        return resources.getString(R.string.app_name)
    }

    override fun getPluginDescription(): String {
        return resources.getString(R.string.plugin_description)
    }

    override fun getPluginAuthor(): String {
        return resources.getString(R.string.plugin_author)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getSmallIcon(): Drawable {
        return resources.getDrawable(R.drawable.nodejs_logo, context.theme)
    }

    override fun getSubscript(): String {
        return resources.getString(R.string.subscript)
    }

}