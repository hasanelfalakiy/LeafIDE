package leaf.plugin.nodejs.creator

import androidx.fragment.app.Fragment
import io.github.caimucheng.leaf.plugin.creator.FragmentCreator
import leaf.plugin.nodejs.fragment.NewProjectFragment

object APPFragmentCreator : FragmentCreator {

    override fun onNewProject(): Fragment {
        return NewProjectFragment()
    }

}