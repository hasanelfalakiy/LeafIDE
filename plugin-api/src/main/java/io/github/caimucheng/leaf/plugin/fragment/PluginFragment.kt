package io.github.caimucheng.leaf.plugin.fragment

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import io.github.caimucheng.leaf.plugin.action.ActionHolder

abstract class PluginFragment : Fragment() {

    protected lateinit var actionHolder: ActionHolder
        private set

    @CallSuper
    open fun onPrepareActionHolder(actionHolder: ActionHolder) {
        this.actionHolder = actionHolder
    }

}