package leaf.nodejs.module.creator

import io.github.caimucheng.leaf.module.creator.FragmentCreator
import leaf.nodejs.module.fragment.NewProjectFragment

object NodeJSFragmentCreator : FragmentCreator {

    override fun onNewProject(): NewProjectFragment {
        return NewProjectFragment()
    }

}