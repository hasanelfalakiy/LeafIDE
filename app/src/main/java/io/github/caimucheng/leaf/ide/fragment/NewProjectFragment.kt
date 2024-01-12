package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentNewProjectBinding
import io.github.caimucheng.leaf.ide.model.moduleSupport
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.ModuleState
import io.github.caimucheng.leaf.module.action.ActionHolder
import io.github.caimucheng.leaf.module.fragment.ModuleFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NewProjectFragment : Fragment() {

    private lateinit var viewBinding: FragmentNewProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentNewProjectBinding.inflate(inflater, container, false)
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
                            val onNewProjectFragment = fragmentCreator.onNewProject()
                            setupFragment(onNewProjectFragment)
                            childFragmentManager.beginTransaction()
                                .setCustomAnimations(
                                    androidx.navigation.ui.R.anim.nav_default_enter_anim,
                                    androidx.navigation.ui.R.anim.nav_default_exit_anim,
                                    androidx.navigation.ui.R.anim.nav_default_pop_enter_anim,
                                    androidx.navigation.ui.R.anim.nav_default_pop_exit_anim
                                )
                                .replace(R.id.content, onNewProjectFragment)
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

    private fun setupFragment(onNewProjectFragment: ModuleFragment) {
        val actionHolder = ActionHolder(
            onPopBackStack = { findNavController().popBackStack() },
            onPopBackHome = {
                if (it) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        AppViewModel.intent.send(AppIntent.Refresh)
                        findNavController().navigate(R.id.action_newProjectFragment_to_mainFragment)
                    }
                } else {
                    findNavController().navigate(R.id.action_newProjectFragment_to_mainFragment)
                }
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
        onNewProjectFragment.onPrepareActionHolder(actionHolder)
    }

    private fun setupIconButton() {
        viewBinding.iconButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}