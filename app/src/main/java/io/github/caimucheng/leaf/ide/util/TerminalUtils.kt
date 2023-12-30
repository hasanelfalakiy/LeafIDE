package io.github.caimucheng.leaf.ide.util

fun systemEnvironment() = mutableMapOf(
    "TERM" to "xterm-256color",
    "ANDROID_ROOT" to System.getenv("ANDROID_ROOT")!!,
    "ANDROID_DATA" to System.getenv("ANDROID_DATA")!!,
    "COLORTERM" to "truecolor",
)