package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentNewProjectBinding
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.PluginState
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
        val packageName =
            arguments?.getString("packageName") ?: return run { findNavController().popBackStack() }

        viewLifecycleOwner.lifecycleScope.launch {
            AppViewModel.state.collectLatest {
                when (it.pluginState) {
                    PluginState.Loading -> {
                        viewBinding.content.visibility = View.GONE
                        viewBinding.placeholder.visibility = View.GONE
                        viewBinding.loading.visibility = View.VISIBLE
                    }

                    PluginState.Done -> {
                        viewBinding.loading.visibility = View.GONE
                        val plugin =
                            it.plugins.find { plugin -> plugin.packageName == packageName }
                        if (plugin != null) {
                            viewBinding.placeholder.visibility = View.GONE
                            viewBinding.content.visibility = View.VISIBLE

                            val fragmentCreator = plugin.pluginAPP.getFragmentCreator()
                            childFragmentManager.beginTransaction()
                                .replace(R.id.content, fragmentCreator.onNewProject())
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

    private fun setupIconButton() {
        viewBinding.iconButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}