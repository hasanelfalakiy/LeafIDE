package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.adapter.TemplateAdapter
import io.github.caimucheng.leaf.ide.databinding.FragmentTemplateProjectBinding
import io.github.caimucheng.leaf.ide.model.Module
import io.github.caimucheng.leaf.ide.model.isEnabled
import io.github.caimucheng.leaf.ide.model.moduleSupport
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.ModuleState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TemplateProjectFragment : Fragment() {

    private lateinit var viewBinding: FragmentTemplateProjectBinding

    private val modules by lazy {
        ArrayList<Module>(AppViewModel.state.value.modules.filter { it.isEnabled })
    }

    private val adapter by lazy {
        TemplateAdapter(
            context = requireContext(),
            modules = modules,
            onItemClick = { module ->
                findNavController()
                    .navigate(
                        R.id.action_templateProjectFragment_to_newProjectFragment,
                        bundleOf(
                            "moduleSupport" to module.moduleSupport
                        )
                    )
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentTemplateProjectBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppViewModel.state.map {
                    it.copy(
                        modules = it.modules.filter { module -> module.isEnabled }
                    )
                }.collectLatest {
                    when (it.moduleState) {
                        ModuleState.Loading -> {
                            viewBinding.recyclerView.visibility = View.GONE
                            viewBinding.placeholder.visibility = View.GONE
                            viewBinding.loading.visibility = View.VISIBLE
                        }

                        ModuleState.Done -> {
                            viewBinding.loading.visibility = View.GONE
                            modules.clear()
                            modules.addAll(it.modules)
                            if (modules.isNotEmpty()) {
                                viewBinding.placeholder.visibility = View.GONE
                                viewBinding.recyclerView.visibility = View.VISIBLE
                            } else {
                                viewBinding.recyclerView.visibility = View.GONE
                                viewBinding.placeholder.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        viewBinding.materialToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        viewBinding.recyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        viewBinding.recyclerView.adapter = adapter
    }

}