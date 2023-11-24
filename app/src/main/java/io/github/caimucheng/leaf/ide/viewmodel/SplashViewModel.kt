package io.github.caimucheng.leaf.ide.viewmodel

import android.text.SpannedString
import androidx.lifecycle.viewModelScope
import io.github.caimucheng.leaf.common.mvi.MVIViewModel
import io.github.caimucheng.leaf.common.mvi.UiIntent
import io.github.caimucheng.leaf.common.mvi.UiState
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.depository.SplashDepository
import io.github.caimucheng.leaf.ide.util.language
import kotlinx.coroutines.launch

enum class SplashPage {
    PrivacyPolicy,
    UserAgreement,
    LaunchMode
}

enum class LaunchMode {
    LaunchFromExteralStorage,
    LaunchFromInternalStorage
}

data class SplashUiState(
    val titleResId: Int = R.string.privacy_policy,
    val content: SpannedString? = null,
    val page: SplashPage = SplashPage.PrivacyPolicy,
    val selectedLaunchMode: LaunchMode = LaunchMode.LaunchFromExteralStorage,
    val initializedLaunchMode: Boolean = false
) : UiState()

sealed class SplashUiIntent : UiIntent() {
    data object NextPage : SplashUiIntent()
    data object PreviousPage : SplashUiIntent()

    data object InitializeLaunchMode : SplashUiIntent()

    data class GetContent(val page: SplashPage) : SplashUiIntent()

    data class SelectLaunchMode(val launchMode: LaunchMode) : SplashUiIntent()
}

class SplashViewModel : MVIViewModel<SplashUiState, SplashUiIntent>() {

    private val splashDepository = SplashDepository()

    override fun initialValue(): SplashUiState {
        return SplashUiState()
    }

    override fun handleIntent(intent: SplashUiIntent, currentState: SplashUiState) {
        when (intent) {
            is SplashUiIntent.GetContent -> {
                getContent(AppContext.current.language, intent.page)
            }

            SplashUiIntent.NextPage -> {
                nextPage()
            }

            SplashUiIntent.PreviousPage -> {
                previousPage()
            }

            is SplashUiIntent.SelectLaunchMode -> {
                selectLaunchMode(intent.launchMode)
            }

            SplashUiIntent.InitializeLaunchMode -> {
                initializeLaunchMode(state.value.selectedLaunchMode)
            }
        }
    }

    private fun initializeLaunchMode(launchMode: LaunchMode) {
        viewModelScope.launch {
            splashDepository.initializeLaunchMode(launchMode)
            setState(state.value.copy(initializedLaunchMode = true))
        }
    }

    private fun selectLaunchMode(launchMode: LaunchMode) {
        viewModelScope.launch {
            setState(state.value.copy(selectedLaunchMode = launchMode))
        }
    }

    private fun previousPage() {
        viewModelScope.launch {
            when (state.value.page) {
                SplashPage.PrivacyPolicy -> {}

                SplashPage.UserAgreement -> {
                    val content =
                        splashDepository.getContent(
                            AppContext.current.language,
                            SplashPage.PrivacyPolicy
                        )

                    setState(
                        state.value.copy(
                            titleResId = R.string.privacy_policy,
                            content = content,
                            page = SplashPage.PrivacyPolicy
                        )
                    )
                }

                SplashPage.LaunchMode -> {
                    val content =
                        splashDepository.getContent(
                            AppContext.current.language,
                            SplashPage.UserAgreement
                        )

                    setState(
                        state.value.copy(
                            titleResId = R.string.user_agreement,
                            content = content,
                            page = SplashPage.UserAgreement
                        )
                    )
                }
            }
        }
    }

    private fun nextPage() {
        viewModelScope.launch {
            when (state.value.page) {
                SplashPage.PrivacyPolicy -> {
                    val content =
                        splashDepository.getContent(
                            AppContext.current.language,
                            SplashPage.UserAgreement
                        )

                    setState(
                        state.value.copy(
                            titleResId = R.string.user_agreement,
                            content = content,
                            page = SplashPage.UserAgreement
                        )
                    )
                }

                SplashPage.UserAgreement -> {
                    val content =
                        splashDepository.getContent(
                            AppContext.current.language,
                            SplashPage.LaunchMode
                        )

                    setState(
                        state.value.copy(
                            titleResId = R.string.launch_mode,
                            content = content,
                            page = SplashPage.LaunchMode
                        )
                    )
                }

                SplashPage.LaunchMode -> {}
            }
        }
    }

    private fun getContent(language: String, page: SplashPage) {
        viewModelScope.launch {
            val content = splashDepository.getContent(language, page)
            setState(state.value.copy(content = content))
        }
    }

}