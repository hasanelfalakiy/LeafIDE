package io.github.caimucheng.leaf.compose.ui.theme

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val ModernLightColorScheme = lightColorScheme(
    Color(0xff4d5c9200000000UL),
    Color(0xffffffff00000000UL),
    Color(0xffdce1ff00000000UL),
    Color(0xff03174b00000000UL),
    Color(0xffb6c4ff00000000UL),
    Color(0xff595d7200000000UL),
    Color(0xffffffff00000000UL),
    Color(0xffdee1f900000000UL),
    Color(0xff161b2c00000000UL),
    Color(0xff75546f00000000UL),
    Color(0xffffffff00000000UL),
    Color(0xffffd7f600000000UL),
    Color(0xff2c122a00000000UL),
    Color(0xfffefbff00000000UL),
    Color(0xff1b1b1f00000000UL),
    Color(0xfffefbff00000000UL),
    Color(0xff1b1b1f00000000UL),
    Color(0xffe2e1ec00000000UL),
    Color(0xff45464f00000000UL),
    Color(0xff4d5c9200000000UL),
    Color(0xff30303400000000UL),
    Color(0xfff2f0f400000000UL),
    Color(0xffb3261e00000000UL),
    Color(0xffffffff00000000UL),
    Color(0xfff9dedc00000000UL),
    Color(0xff410e0b00000000UL),
    Color(0xff75757f00000000UL),
    Color(0xffcac4d000000000UL),
    Color(0xff00000000000000UL)
)

val ModernDarkColorScheme = darkColorScheme(
    Color(0xffb6c4ff00000000UL),
    Color(0xff1d2d6100000000UL),
    Color(0xff35447900000000UL),
    Color(0xffdce1ff00000000UL),
    Color(0xff4d5c9200000000UL),
    Color(0xffc2c5dd00000000UL),
    Color(0xff2b304200000000UL),
    Color(0xff42465900000000UL),
    Color(0xffdee1f900000000UL),
    Color(0xffe3bada00000000UL),
    Color(0xff43274000000000UL),
    Color(0xff5b3d5700000000UL),
    Color(0xffffd7f600000000UL),
    Color(0xff1b1b1f00000000UL),
    Color(0xffe4e1e600000000UL),
    Color(0xff1b1b1f00000000UL),
    Color(0xffe4e1e600000000UL),
    Color(0xff45464f00000000UL),
    Color(0xffc6c6d000000000UL),
    Color(0xffb6c4ff00000000UL),
    Color(0xffe4e1e600000000UL),
    Color(0xff30303400000000UL),
    Color(0xfff2b8b500000000UL),
    Color(0xff60141000000000UL),
    Color(0xff8c1d1800000000UL),
    Color(0xfff9dedc00000000UL),
    Color(0xff90909a00000000UL),
    Color(0xff49454f00000000UL),
    Color(0xff00000000000000UL)
)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LeafIDETheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = if (darkTheme) ModernDarkColorScheme else ModernLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun LeafIDETheme(
    proxyContext: Context,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalContext provides proxyContext) {
        LeafIDETheme(darkTheme, content)
    }
}