package leaf.nodejs.module.creator

import io.github.caimucheng.leaf.module.creator.FragmentCreator
import io.github.caimucheng.leaf.module.fragment.ModuleFragment
import leaf.nodejs.module.fragment.ManageNodeJSModuleFragment
import leaf.nodejs.module.fragment.NewProjectFragment

object NodeJSFragmentCreator : FragmentCreator {

    override fun onNewProject(): NewProjectFragment {
        return NewProjectFragment()
    }

    override fun onManageModule(): ModuleFragment {
        return ManageNodeJSModuleFragment()
    }

}