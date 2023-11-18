package leaf.plugin.nodejs

import android.content.Context
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

}