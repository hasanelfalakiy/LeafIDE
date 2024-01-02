package io.github.caimucheng.leaf.ide.util

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannedString
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.text.buildSpannedString

fun parseProtocol(text: String): SpannedString {
    // first:text second:type
    val tokens: MutableList<Pair<String, String>> = ArrayList()
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
                    tokens.add(Pair(cache.toString(), "text"))
                    cache.clear()
                }
            }

            ch == '>' && inTitleContext -> {
                inTitleContext = false
                if (cache.isNotEmpty()) {
                    tokens.add(Pair(cache.toString(), "title"))
                    cache.clear()
                }
            }

            ch == '(' && !inBoldContext -> {
                inBoldContext = true
                if (cache.isNotEmpty()) {
                    tokens.add(Pair(cache.toString(), "text"))
                    cache.clear()
                }
            }

            ch == ')' && inBoldContext -> {
                inBoldContext = false
                if (cache.isNotEmpty()) {
                    tokens.add(Pair(cache.toString(), "bold"))
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
            tokens.add(Pair(cache.toString(), "title"))
            cache.clear()
        } else {
            tokens.add(Pair(cache.toString(), "text"))
            cache.clear()
        }
        if (inBoldContext) {
            tokens.add(Pair(cache.toString(), "bold"))
            cache.clear()
        } else {
            tokens.add(Pair(cache.toString(), "text"))
            cache.clear()
        }
    }

    return buildSpannedString {
        for (token in tokens) {
            when (token.second) {
                "title" -> {
                    val start = length
                    if (isNotEmpty()) {
                        appendLine()
                    }
                    append(
                        token.first
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
                        token.first,
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
                        token.first,
                    )
                }
            }
        }
    }
}