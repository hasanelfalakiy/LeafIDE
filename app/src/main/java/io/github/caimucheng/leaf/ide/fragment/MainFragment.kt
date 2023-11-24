package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentMainBinding
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainViewModel.initialize()
        viewBinding = FragmentMainBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomNavigation()
        if (!AppViewModel.state.value.isRefreshed) {
            viewLifecycleOwner.lifecycleScope.launch {
                AppViewModel.intent.send(AppIntent.Refresh)
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