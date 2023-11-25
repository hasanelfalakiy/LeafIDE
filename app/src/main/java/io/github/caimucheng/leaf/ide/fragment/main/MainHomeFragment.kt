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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.adapter.MainHomeAdapter
import io.github.caimucheng.leaf.ide.databinding.FragmentMainHomeBinding
import io.github.caimucheng.leaf.ide.fragment.MainFragment
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.PluginState
import io.github.caimucheng.leaf.ide.viewmodel.ProjectState
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppViewModel.state.collectLatest {
                    when (it.projectState) {
                        ProjectState.Loading -> {
                            viewBinding.content.visibility = View.GONE
                            viewBinding.placeholder.visibility = View.GONE
                            viewBinding.loading.visibility = View.VISIBLE
                        }

                        ProjectState.Done -> {
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
        val rootFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        val mainFragment = rootFragment?.childFragmentManager?.fragments?.get(0) as? MainFragment

        viewBinding.fab1.setOnClickListener {
            mainFragment?.findNavController()
                ?.navigate(R.id.action_mainFragment_to_templateProjectFragment)
        }
        viewBinding.fab2.setOnClickListener {
            mainFragment?.findNavController()
                ?.navigate(R.id.action_mainFragment_to_templateProjectFragment)
        }
    }

    private fun setupRecyclerView() {
        viewBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.recyclerView.adapter = adapter
    }

}