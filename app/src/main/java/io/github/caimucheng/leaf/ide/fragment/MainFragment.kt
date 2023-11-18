package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentMainBinding
import io.github.caimucheng.leaf.ide.viewmodel.MainIntent
import io.github.caimucheng.leaf.ide.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMainBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNavigation()
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.intent.send(MainIntent.Initialize)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.state.collectLatest {
                    val isLoadingPage = it.isLoadingPage
                    if (isLoadingPage) {
                        viewBinding.content.visibility = View.GONE
                        viewBinding.loading.visibility = View.VISIBLE
                    } else {
                        viewBinding.loading.visibility = View.GONE
                        viewBinding.content.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        val pageNavHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val pageNavController = pageNavHostFragment.navController
        viewBinding.bottomNavigationBar.setupWithNavController(pageNavController)
    }

}