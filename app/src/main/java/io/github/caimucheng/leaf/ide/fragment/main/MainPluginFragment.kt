package io.github.caimucheng.leaf.ide.fragment.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.caimucheng.leaf.ide.adapter.MainPluginAdapter
import io.github.caimucheng.leaf.ide.databinding.FragmentMainPluginBinding
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.MainViewModel

class MainPluginFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainPluginBinding

    private val mainViewModel: MainViewModel by viewModels()

    private val adapter by lazy {
        MainPluginAdapter(requireContext(), AppViewModel.state.value.plugins)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMainPluginBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()


    }

    private fun setupRecyclerView() {
        viewBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.recyclerView.adapter = adapter
    }

}