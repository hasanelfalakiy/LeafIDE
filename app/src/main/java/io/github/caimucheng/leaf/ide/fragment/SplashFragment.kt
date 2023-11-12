package io.github.caimucheng.leaf.ide.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.databinding.FragmentSplashBinding
import io.github.caimucheng.leaf.ide.util.launchModeSharedPreferences
import io.github.caimucheng.leaf.ide.viewmodel.LaunchMode
import io.github.caimucheng.leaf.ide.viewmodel.SplashPage
import io.github.caimucheng.leaf.ide.viewmodel.SplashUiIntent
import io.github.caimucheng.leaf.ide.viewmodel.SplashViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var viewBinding: FragmentSplashBinding

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSplashBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = Navigation.findNavController(view)
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
                val writable = results[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: false
                val readable = results[Manifest.permission.READ_EXTERNAL_STORAGE] ?: false

                when {
                    writable && readable -> {
                        viewLifecycleOwner.lifecycleScope.launch {
                            splashViewModel.intent.send(SplashUiIntent.InitializeLaunchMode)
                        }
                    }

                    writable -> {
                        Toasty.error(requireContext(), R.string.no_readable, Toasty.LENGTH_SHORT)
                            .show()
                    }

                    readable -> {
                        Toasty.error(requireContext(), R.string.no_writable, Toasty.LENGTH_SHORT)
                            .show()
                    }

                    else -> {
                        Toasty.error(
                            requireContext(),
                            R.string.no_writable_and_readable,
                            Toasty.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        val launchModeSharedPreferences = AppContext.current.launchModeSharedPreferences
        val settedLaunchMode = launchModeSharedPreferences.getString("launchMode", null)
        if (settedLaunchMode != null) {
            when (LaunchMode.valueOf(settedLaunchMode)) {
                LaunchMode.LaunchFromExteralStorage -> {
                    val writable = ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                    val readable = ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                    if (writable && readable) {
                        navController.navigate(R.id.action_splashFragment_to_mainFragment)
                        return
                    }
                }

                LaunchMode.LaunchFromInternalStorage -> {
                    navController.navigate(R.id.action_splashFragment_to_mainFragment)
                    return
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                splashViewModel.intent.send(SplashUiIntent.GetContent(splashViewModel.state.value.page))
                splashViewModel.state.collectLatest {
                    val newTitle = getString(it.titleResId)
                    if (viewBinding.toolbarLayout.title != newTitle) {
                        viewBinding.toolbarLayout.visibility = View.INVISIBLE
                        viewBinding.toolbarLayout.title = newTitle
                        viewBinding.toolbarLayout.visibility = View.VISIBLE
                    }
                    when (it.page) {
                        SplashPage.PrivacyPolicy -> {
                            viewBinding.privacyPolicyContent.text =
                                it.content

                            viewBinding.userAgreementPage.visibility = View.GONE
                            viewBinding.launchModePage.visibility = View.GONE
                            viewBinding.privacyPolicyPage.visibility = View.VISIBLE
                            viewBinding.previousIcon.visibility = View.GONE
                            viewBinding.closeIcon.visibility = View.VISIBLE
                        }

                        SplashPage.UserAgreement -> {
                            viewBinding.userAgreementContent.text =
                                it.content

                            viewBinding.privacyPolicyPage.visibility = View.GONE
                            viewBinding.launchModePage.visibility = View.GONE
                            viewBinding.userAgreementPage.visibility = View.VISIBLE
                            viewBinding.closeIcon.visibility = View.GONE
                            viewBinding.doneIcon.visibility = View.GONE
                            viewBinding.previousIcon.visibility = View.VISIBLE
                            viewBinding.nextIcon.visibility = View.VISIBLE
                        }

                        SplashPage.LaunchMode -> {
                            viewBinding.launchModeContent.text =
                                it.content

                            viewBinding.privacyPolicyPage.visibility = View.GONE
                            viewBinding.userAgreementPage.visibility = View.GONE
                            viewBinding.launchModePage.visibility = View.VISIBLE
                            viewBinding.closeIcon.visibility = View.GONE
                            viewBinding.nextIcon.visibility = View.GONE
                            viewBinding.previousIcon.visibility = View.VISIBLE
                            viewBinding.doneIcon.visibility = View.VISIBLE
                        }
                    }
                    when (it.selectedLaunchMode) {
                        LaunchMode.LaunchFromExteralStorage -> {
                            viewBinding.launchFromExternalStorageRadioButton.isChecked = true
                            viewBinding.launchFromInternalStorageRadioButton.isChecked = false
                        }

                        LaunchMode.LaunchFromInternalStorage -> {
                            viewBinding.launchFromInternalStorageRadioButton.isChecked = true
                            viewBinding.launchFromExternalStorageRadioButton.isChecked = false
                        }
                    }
                    if (it.initializedLaunchMode) {
                        navController.navigate(R.id.action_splashFragment_to_mainFragment)
                        cancel()
                    }
                }
            }
        }

        viewBinding.previousButton.setOnClickListener {
            if (splashViewModel.state.value.page == SplashPage.PrivacyPolicy) {
                requireActivity().finish()
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                splashViewModel.intent.send(SplashUiIntent.PreviousPage)
            }
        }

        viewBinding.nextButton.setOnClickListener {
            if (splashViewModel.state.value.page == SplashPage.LaunchMode) {
                when (splashViewModel.state.value.selectedLaunchMode) {
                    LaunchMode.LaunchFromExteralStorage -> activityResultLauncher.launch(
                        arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    )

                    LaunchMode.LaunchFromInternalStorage -> viewLifecycleOwner.lifecycleScope.launch {
                        splashViewModel.intent.send(SplashUiIntent.InitializeLaunchMode)
                    }
                }
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                splashViewModel.intent.send(SplashUiIntent.NextPage)
            }
        }

        viewBinding.launchFromExternalStorageCard.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                splashViewModel.intent.send(SplashUiIntent.SelectLaunchMode(LaunchMode.LaunchFromExteralStorage))
            }
        }

        viewBinding.launchFromInternalStorageCard.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                splashViewModel.intent.send(SplashUiIntent.SelectLaunchMode(LaunchMode.LaunchFromInternalStorage))
            }
        }
    }

}