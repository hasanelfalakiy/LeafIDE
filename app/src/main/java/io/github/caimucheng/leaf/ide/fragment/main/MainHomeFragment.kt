package io.github.caimucheng.leaf.ide.fragment.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.adapter.MainHomeAdapter
import io.github.caimucheng.leaf.ide.databinding.FragmentMainHomeBinding
import io.github.caimucheng.leaf.ide.enums.ListState
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.model.moduleSupport
import io.github.caimucheng.leaf.ide.util.findGlobalNavController
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainHomeFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainHomeBinding

    private val projects by lazy {
        ArrayList<Project>(AppViewModel.state.value.projects)
    }

    private val adapter by lazy {
        MainHomeAdapter(requireContext(), projects)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMainHomeBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFab()
        setupToolbar()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppViewModel.state.collectLatest {
                    when (it.projectState) {
                        ListState.Loading -> {
                            viewBinding.content.visibility = View.GONE
                            viewBinding.placeholder.visibility = View.GONE
                            viewBinding.loading.visibility = View.VISIBLE
                        }

                        ListState.Done -> {
                            viewBinding.loading.visibility = View.GONE
                            projects.clear()
                            projects.addAll(it.projects)
                            adapter.notifyDataSetChanged()
                            if (projects.isNotEmpty()) {
                                viewBinding.placeholder.visibility = View.GONE
                                viewBinding.content.visibility = View.VISIBLE
                            } else {
                                viewBinding.content.visibility = View.GONE
                                viewBinding.placeholder.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupFab() {
        val navController = findGlobalNavController()

        viewBinding.fab1.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_templateProjectFragment)
        }
        viewBinding.fab2.setOnClickListener {
            navController.navigate(R.id.action_mainFragment_to_templateProjectFragment)
        }
    }

    private fun setupRecyclerView() {
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        adapter.setOnItemClickListener { _, position ->
            val bundle = Bundle()
            bundle.putString("projectPath", projects[position].projectPath)
            bundle.putString("moduleSupport", projects[position].moduleSupport)
            findGlobalNavController().navigate(
                R.id.action_mainFragment_to_projectEditorFragment,
                bundle
            )
        }
        adapter.setOnItemLongClickListener { _, _ ->

        }
        viewBinding.recyclerView.adapter = adapter
    }

    private fun setupToolbar() {
        viewBinding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.settings) {
                findGlobalNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                return@setOnMenuItemClickListener true
            } else {
                return@setOnMenuItemClickListener false
            }
        }
    }
}