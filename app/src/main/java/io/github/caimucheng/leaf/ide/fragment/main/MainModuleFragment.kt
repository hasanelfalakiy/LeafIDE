package io.github.caimucheng.leaf.ide.fragment.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.adapter.MainModuleAdapter
import io.github.caimucheng.leaf.ide.databinding.FragmentMainModuleBinding
import io.github.caimucheng.leaf.ide.model.Module
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.ModuleState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainModuleFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainModuleBinding

    private val modules by lazy {
        ArrayList<Module>(AppViewModel.state.value.modules)
    }

    private val adapter by lazy {
        MainModuleAdapter(
            context = requireContext(),
            modules = modules,
            onToggle = {
                viewLifecycleOwner.lifecycleScope.launch {
                    AppViewModel.intent.send(AppIntent.Refresh)
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMainModuleBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppViewModel.state.collectLatest {
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
                            adapter.notifyDataSetChanged()
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
        viewBinding.toolbar.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_module, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }

        }, viewLifecycleOwner)
    }

    private fun setupRecyclerView() {
        viewBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.recyclerView.adapter = adapter
    }

}