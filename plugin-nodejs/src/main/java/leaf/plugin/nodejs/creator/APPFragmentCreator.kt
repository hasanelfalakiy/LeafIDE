package leaf.plugin.nodejs.creator

import androidx.fragment.app.Fragment
import io.github.caimucheng.leaf.plugin.creator.FragmentCreator
import io.github.caimucheng.leaf.plugin.fragment.PluginFragment
import leaf.plugin.nodejs.fragment.NewProjectFragment

object APPFragmentCreator : FragmentCreator {

    override fun onNewProject(): PluginFragment {
        return NewProjectFragment()
    }

}