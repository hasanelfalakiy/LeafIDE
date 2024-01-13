package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentBaseModuleBinding
import io.github.caimucheng.leaf.ide.model.moduleSupport
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.ModuleState
import io.github.caimucheng.leaf.module.action.ActionHolder
import io.github.caimucheng.leaf.module.creator.FragmentCreator
import io.github.caimucheng.leaf.module.fragment.ModuleFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

abstract class BaseModuleFragment : Fragment() {

    private lateinit var viewBinding: FragmentBaseModuleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentBaseModuleBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupIconButton()
        val moduleSupport =
            arguments?.getString("moduleSupport") ?: return run { findNavController().popBackStack() }

        viewLifecycleOwner.lifecycleScope.launch {
            AppViewModel.state.collectLatest {
                when (it.moduleState) {
                    ModuleState.Loading -> {
                        viewBinding.content.visibility = View.GONE
                        viewBinding.placeholder.visibility = View.GONE
                        viewBinding.loading.visibility = View.VISIBLE
                    }

                    ModuleState.Done -> {
                        viewBinding.loading.visibility = View.GONE
                        val module =
                            it.modules.find { module -> module.moduleSupport == moduleSupport }
                        if (module != null) {
                            viewBinding.placeholder.visibility = View.GONE
                            viewBinding.content.visibility = View.VISIBLE

                            val fragmentCreator = module.fragmentCreator
                            val moduleFragment = getModuleFragment(fragmentCreator)
                            setupFragment(moduleFragment)
                            childFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    androidx.navigation.ui.R.anim.nav_default_enter_anim,
                                    androidx.navigation.ui.R.anim.nav_default_exit_anim,
                                    androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                                    androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
                                )
                                .replace(R.id.content, moduleFragment)
                                .commit()
                        } else {
                            viewBinding.content.visibility = View.GONE
                            viewBinding.placeholder.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setupFragment(moduleFragment: ModuleFragment) {
        val actionHolder = ActionHolder(
            onPopBackStack = { findNavController().popBackStack() },
            onPopBackHome = {
                onPopBackHome(it)
            },
            onStartFragment = {
                setupFragment(it)
                childFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        androidx.navigation.ui.R.anim.nav_default_enter_anim,
                        androidx.navigation.ui.R.anim.nav_default_exit_anim,
                        androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                        androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
                    )
                    .replace(R.id.content, it)
                    .addToBackStack(null)
                    .commit()
            },
            onReplaceFragment = {
                setupFragment(it)
                childFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        androidx.navigation.ui.R.anim.nav_default_enter_anim,
                        androidx.navigation.ui.R.anim.nav_default_exit_anim,
                        androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                        androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
                    )
                    .replace(R.id.content, it)
                    .commit()
            }
        )
        moduleFragment.onPrepareActionHolder(actionHolder)
    }

    private fun setupIconButton() {
        viewBinding.iconButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    protected abstract fun getModuleFragment(fragmentCreator: FragmentCreator): ModuleFragment

    protected abstract fun onPopBackHome(refreshModule: Boolean)

}