package io.github.caimucheng.leaf.ide.fragment

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.module.creator.FragmentCreator
import io.github.caimucheng.leaf.module.fragment.ModuleFragment
import kotlinx.coroutines.launch

class ModuleManagementFragment : BaseModuleFragment() {

    override fun getModuleFragment(fragmentCreator: FragmentCreator): ModuleFragment {
        return fragmentCreator.onManageModule()
    }

    override fun onPopBackHome(refreshModule: Boolean) {
        if (refreshModule) {
            viewLifecycleOwner.lifecycleScope.launch {
                AppViewModel.intent.send(AppIntent.Refresh)
            }
        }
        findNavController().navigateUp()
    }

}