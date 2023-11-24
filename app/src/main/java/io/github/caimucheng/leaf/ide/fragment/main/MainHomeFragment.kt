package io.github.caimucheng.leaf.ide.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.adapter.MainHomeAdapter
import io.github.caimucheng.leaf.ide.databinding.FragmentMainHomeBinding
import io.github.caimucheng.leaf.ide.fragment.MainFragment
import io.github.caimucheng.leaf.ide.model.Project

class MainHomeFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainHomeBinding

    private val projects by lazy {
        ArrayList<Project>(0)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        val mainFragment = rootFragment?.childFragmentManager?.fragments?.get(0) as? MainFragment
        val navController = mainFragment?.findNavController()
        setupRecyclerView()
        viewBinding.fab.setOnClickListener {
            navController?.navigate(R.id.action_mainFragment_to_templateProjectFragment)
        }
    }

    private fun setupRecyclerView() {
        viewBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.recyclerView.adapter = adapter
    }

}