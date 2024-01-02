package io.github.caimucheng.leaf.ide.depository

import android.text.SpannedString
import androidx.core.content.edit
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.util.getTextFromAssets
import io.github.caimucheng.leaf.ide.util.launchModeSharedPreferences
import io.github.caimucheng.leaf.ide.util.parseProtocol
import io.github.caimucheng.leaf.ide.viewmodel.LaunchMode
import io.github.caimucheng.leaf.ide.viewmodel.SplashPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SplashDepository {
    suspend fun getContent(language: String, page: SplashPage): SpannedString? {
        return withContext(Dispatchers.IO) {
            val context = AppContext.current
            val target = when (page) {
                SplashPage.PrivacyPolicy -> "PrivacyPolicy"
                SplashPage.UserAgreement -> "UserAgreement"
                SplashPage.LaunchMode -> "LaunchMode"
            }
            runCatching {
                parseProtocol(context.getTextFromAssets("protocol/$language/$target.txt"))
            }.getOrNull()
        }
    }

    suspend fun initializeLaunchMode(launchMode: LaunchMode) {
        return withContext(Dispatchers.IO) {
            val sharedPreferences = AppContext.current.launchModeSharedPreferences
            sharedPreferences.edit {
                putString("launchMode", launchMode.name)
            }
        }
    }
}