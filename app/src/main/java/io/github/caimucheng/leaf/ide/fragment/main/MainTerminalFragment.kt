package io.github.caimucheng.leaf.ide.fragment.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.termux.terminal.TerminalSession
import com.termux.terminal.TerminalSessionClient
import com.termux.view.TerminalViewClient
import io.github.caimucheng.leaf.ide.databinding.FragmentMainTerminalBinding


class MainTerminalFragment : Fragment(), TerminalViewClient, TerminalSessionClient {

    private lateinit var viewBinding: FragmentMainTerminalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMainTerminalBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val terminalView = viewBinding.terminalView
        terminalView.setBackgroundColor(Color.BLACK)
        terminalView.setTextSize(30)
        terminalView.setTerminalViewClient(this)
        terminalView.attachSession(createNewSession())
    }

    private fun createNewSession(): TerminalSession {
        return TerminalSession(
            "/system/bin/sh",
            requireContext().filesDir.absolutePath,
            arrayOf(),
            arrayOf(),
            0,
            this
        )
    }

    private fun changeFontSize(increase: Boolean) {
        val termView = viewBinding.terminalView
        val changedSize = (if (increase) 1 else -1) * 2
        termView.setTextSize(30 + changedSize)
    }

    override fun onScale(scale: Float): Float {
        if (scale < 0.9f || scale > 1.1f) {
            val increase = scale > 1f
            changeFontSize(increase)
            return 1.0f
        }
        return scale
    }

    override fun onSingleTapUp(e: MotionEvent?) {
        (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(viewBinding.terminalView, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun shouldBackButtonBeMappedToEscape(): Boolean {
        return false
    }

    override fun shouldEnforceCharBasedInput(): Boolean {
        return false
    }

    override fun shouldUseCtrlSpaceWorkaround(): Boolean {
        return false
    }

    override fun isTerminalViewSelected(): Boolean {
        return false
    }

    override fun copyModeChanged(copyMode: Boolean) {

    }

    override fun onKeyDown(keyCode: Int, e: KeyEvent?, session: TerminalSession?): Boolean {
        return false
    }

    override fun onKeyUp(keyCode: Int, e: KeyEvent?): Boolean {
        return false
    }

    override fun onLongPress(event: MotionEvent?): Boolean {
        return false
    }

    override fun readControlKey(): Boolean {
        return false
    }

    override fun readAltKey(): Boolean {
        return false
    }

    override fun readShiftKey(): Boolean {
        return false
    }

    override fun readFnKey(): Boolean {
        return false
    }

    override fun onCodePoint(
        codePoint: Int,
        ctrlDown: Boolean,
        session: TerminalSession?
    ): Boolean {
        return false
    }

    override fun onEmulatorSet() {

    }

    override fun onTextChanged(changedSession: TerminalSession) {
        viewBinding.terminalView.onScreenUpdated()
    }

    override fun onTitleChanged(changedSession: TerminalSession) {

    }

    override fun onSessionFinished(finishedSession: TerminalSession) {
        viewBinding.terminalView.attachSession(createNewSession())
    }

    override fun onCopyTextToClipboard(session: TerminalSession, text: String?) {

    }

    override fun onPasteTextFromClipboard(session: TerminalSession?) {

    }

    override fun onBell(session: TerminalSession) {

    }

    override fun onColorsChanged(session: TerminalSession) {

    }

    override fun onTerminalCursorStateChange(state: Boolean) {

    }

    override fun setTerminalShellPid(session: TerminalSession, pid: Int) {

    }

    override fun getTerminalCursorStyle(): Int {
        return 0
    }

    override fun logError(tag: String?, message: String?) {

    }

    override fun logWarn(tag: String?, message: String?) {

    }

    override fun logInfo(tag: String?, message: String?) {

    }

    override fun logDebug(tag: String?, message: String?) {

    }

    override fun logVerbose(tag: String?, message: String?) {

    }

    override fun logStackTraceWithMessage(tag: String?, message: String?, e: Exception?) {

    }

    override fun logStackTrace(tag: String?, e: Exception?) {

    }


}