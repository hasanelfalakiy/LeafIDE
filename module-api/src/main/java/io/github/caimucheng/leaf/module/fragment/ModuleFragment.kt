package io.github.caimucheng.leaf.module.fragment

import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import io.github.caimucheng.leaf.module.action.ActionHolder

abstract class ModuleFragment : Fragment() {

    protected lateinit var actionHolder: ActionHolder
        private set

    @CallSuper
    open fun onPrepareActionHolder(actionHolder: ActionHolder) {
        this.actionHolder = actionHolder
    }

}