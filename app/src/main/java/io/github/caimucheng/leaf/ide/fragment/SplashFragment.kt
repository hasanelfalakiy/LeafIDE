package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import io.github.caimucheng.leaf.ide.databinding.FragmentSplashBinding
import io.github.caimucheng.leaf.ide.viewmodel.SplashPage
import io.github.caimucheng.leaf.ide.viewmodel.SplashUiIntent
import io.github.caimucheng.leaf.ide.viewmodel.SplashViewModel
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
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                splashViewModel.state.collectLatest {
                    viewBinding.toolbarLayout.visibility = View.INVISIBLE
                    viewBinding.toolbarLayout.title = getString(it.titleResId)
                    viewBinding.toolbarLayout.visibility = View.VISIBLE
                    when (it.page) {
                        SplashPage.PrivacyPolicy -> {
                            viewBinding.privacyPolicyPage.privacyPolicyContent.text =
                                it.content

                            viewBinding.userAgreementPage.root.visibility = View.GONE
                            viewBinding.launchModePage.root.visibility = View.GONE
                            viewBinding.privacyPolicyPage.root.visibility = View.VISIBLE
                            viewBinding.previousIcon.visibility = View.GONE
                            viewBinding.closeIcon.visibility = View.VISIBLE
                        }

                        SplashPage.UserAgreement -> {
                            viewBinding.userAgreementPage.userAgreementContent.text =
                                it.content

                            viewBinding.privacyPolicyPage.root.visibility = View.GONE
                            viewBinding.launchModePage.root.visibility = View.GONE
                            viewBinding.userAgreementPage.root.visibility = View.VISIBLE
                            viewBinding.closeIcon.visibility = View.GONE
                            viewBinding.doneIcon.visibility = View.GONE
                            viewBinding.previousIcon.visibility = View.VISIBLE
                            viewBinding.nextIcon.visibility = View.VISIBLE
                        }

                        SplashPage.LaunchMode -> {
                            viewBinding.privacyPolicyPage.root.visibility = View.GONE
                            viewBinding.userAgreementPage.root.visibility = View.GONE
                            viewBinding.launchModePage.root.visibility = View.VISIBLE
                            viewBinding.nextIcon.visibility = View.GONE
                            viewBinding.doneIcon.visibility = View.VISIBLE
                        }
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
                Log.e("Done", "Y")
                return@setOnClickListener
            }
            viewLifecycleOwner.lifecycleScope.launch {
                splashViewModel.intent.send(SplashUiIntent.NextPage)
            }
        }
    }

}