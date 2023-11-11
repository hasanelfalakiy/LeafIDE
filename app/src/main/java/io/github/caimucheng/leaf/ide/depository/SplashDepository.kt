package io.github.caimucheng.leaf.ide.depository

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannedString
import android.text.style.AbsoluteSizeSpan
import android.text.style.LeadingMarginSpan
import android.text.style.LineHeightSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.text.buildSpannedString
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.viewmodel.SplashPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SplashDepository {

    private data class Token(
        val text: String,
        val type: String
    )

    suspend fun getContent(language: String, page: SplashPage): SpannedString? {
        return withContext(Dispatchers.IO) {
            val assets = AppContext.current.assets
            val target = when (page) {
                SplashPage.PrivacyPolicy -> "PrivacyPolicy"
                SplashPage.UserAgreement -> "UserAgreement"
                SplashPage.LaunchMode -> "LaunchMode"
            }
            runCatching {
                assets.open("protocol/$language/$target.txt").bufferedReader().use {
                    parseProtocol(it.readText())
                }
            }.getOrNull()
        }
    }

    private fun parseProtocol(text: String): SpannedString {
        val tokens: MutableList<Token> = ArrayList()
        var index = 0
        var ch: Char
        val cache = StringBuilder()
        var inTitleContext = false
        var inBoldContext = false
        while (index < text.length) {
            ch = text[index]
            when {
                ch == '<' && !inTitleContext -> {
                    inTitleContext = true
                    if (cache.isNotEmpty()) {
                        tokens.add(Token(cache.toString(), "text"))
                        cache.clear()
                    }
                }

                ch == '>' && inTitleContext -> {
                    inTitleContext = false
                    if (cache.isNotEmpty()) {
                        tokens.add(Token(cache.toString(), "title"))
                        cache.clear()
                    }
                }

                ch == '(' && !inBoldContext -> {
                    inBoldContext = true
                    if (cache.isNotEmpty()) {
                        tokens.add(Token(cache.toString(), "text"))
                        cache.clear()
                    }
                }

                ch == ')' && inBoldContext -> {
                    inBoldContext = false
                    if (cache.isNotEmpty()) {
                        tokens.add(Token(cache.toString(), "bold"))
                        cache.clear()
                    }
                }

                ch == '{' -> {
                    cache.append("(")
                }

                ch == '}' -> {
                    cache.append(")")
                }

                else -> {
                    cache.append(ch)
                }
            }
            index++
        }
        if (index == text.length) {
            if (inTitleContext) {
                tokens.add(Token(cache.toString(), "title"))
                cache.clear()
            } else {
                tokens.add(Token(cache.toString(), "text"))
                cache.clear()
            }
            if (inBoldContext) {
                tokens.add(Token(cache.toString(), "bold"))
                cache.clear()
            } else {
                tokens.add(Token(cache.toString(), "text"))
                cache.clear()
            }
        }

        return buildSpannedString {
            for (token in tokens) {
                when (token.type) {
                    "title" -> {
                        val start = length
                        if (isNotEmpty()) {
                            appendLine()
                        }
                        append(
                            token.text
                        )
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            start,
                            length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        setSpan(
                            RelativeSizeSpan(1.3f),
                            start,
                            length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        appendLine()
                    }

                    "bold" -> {
                        val start = length
                        append(
                            token.text,
                        )
                        setSpan(
                            StyleSpan(Typeface.BOLD),
                            start,
                            length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }

                    else -> {
                        append(
                            token.text,
//                                SpanStyle(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f))
                        )
                    }
                }
            }
        }
    }

}