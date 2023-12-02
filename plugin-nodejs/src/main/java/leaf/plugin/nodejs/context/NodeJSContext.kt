package leaf.plugin.nodejs.context

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources

class NodeJSContext(base: Context?, private val resources: Resources) :
    ContextWrapper(base) {

    override fun getResources(): Resources {
        return resources
    }

}