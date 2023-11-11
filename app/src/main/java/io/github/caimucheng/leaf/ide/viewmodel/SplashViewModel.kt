package io.github.caimucheng.leaf.ide.viewmodel

import android.text.SpannableString
import android.text.SpannedString
import androidx.core.content.ContextCompat
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

data class SplashUiState(
    val titleResId: Int = R.string.privacy_policy,
    val content: SpannedString? = null,
    val page: SplashPage = SplashPage.PrivacyPolicy,
    val previousState: SplashUiState? = null
) : UiState()

sealed class SplashUiIntent : UiIntent() {
    data object NextPage : SplashUiIntent()
    data object PreviousPage : SplashUiIntent()

    data class GetContent(val page: SplashPage) : SplashUiIntent()
}

class SplashViewModel : MVIViewModel<SplashUiState, SplashUiIntent>() {

    private val splashDepository = SplashDepository()

    override fun initialValue(): SplashUiState {
        return SplashUiState()
    }

    init {
        viewModelScope.launch {
            intent.send(SplashUiIntent.GetContent(state.value.page))
        }
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
        }
    }

    private fun previousPage() {
        viewModelScope.launch {
            when (val currentPage = state.value.page) {
                SplashPage.PrivacyPolicy -> {

                }

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
                            page = SplashPage.PrivacyPolicy,
                            previousState = state.value
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
                            page = SplashPage.UserAgreement,
                            previousState = state.value
                        )
                    )
                }
            }
        }
    }

    private fun nextPage() {
        viewModelScope.launch {
            when (val currentPage = state.value.page) {
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
                            page = SplashPage.UserAgreement,
                            previousState = state.value
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
                            page = SplashPage.LaunchMode,
                            previousState = state.value
                        )
                    )
                }

                SplashPage.LaunchMode -> {

                }
            }
        }
    }

    private fun getContent(language: String, page: SplashPage) {
        viewModelScope.launch {
            val content = splashDepository.getContent(language, page)
            setState(state.value.copy(content = content, previousState = state.value))
        }
    }

}