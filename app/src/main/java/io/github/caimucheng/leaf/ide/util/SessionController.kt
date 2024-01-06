package io.github.caimucheng.leaf.ide.util

import cn.mucute.merminal.core.TerminalSession
import cn.mucute.merminal.view.ShellTermSession

class SessionController(
    private val command: String? = null,
    private val currentWorkingDirectory: String,
    private val environment: MutableMap<String, String> = systemEnvironment(),
) {
    fun create(callback: TerminalSession.SessionChangedCallback): ShellTermSession {
        val environmentList = mutableListOf<String>()
        for ((t, u) in environment) {
            environmentList.add("$t=$u")
        }
        return ShellTermSession(
            "/system/bin/sh",
            currentWorkingDirectory,
            arrayOf(),
            environmentList.toTypedArray(),
            callback,
            command
        )
    }
}