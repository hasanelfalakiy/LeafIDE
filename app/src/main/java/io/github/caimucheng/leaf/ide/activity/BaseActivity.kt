package io.github.caimucheng.leaf.ide.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import io.github.caimucheng.leaf.common.R

abstract class BaseActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)

        val isDark =
            ContextCompat.getString(this, R.string.is_ui_dark).toBooleanStrictOrNull() ?: false

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            })

        windowInsetsController.isAppearanceLightStatusBars = !isDark
        windowInsetsController.isAppearanceLightNavigationBars = !isDark
    }

}