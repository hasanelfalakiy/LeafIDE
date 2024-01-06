package io.github.caimucheng.leaf.ide.fragment.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.mucute.merminal.view.TermSessionCallback
import cn.mucute.merminal.view.TermViewClient
import io.github.caimucheng.leaf.ide.databinding.FragmentMainTerminalBinding
import io.github.caimucheng.leaf.ide.util.SessionController
import io.github.caimucheng.leaf.ide.util.systemEnvironment

class MainTerminalFragment : Fragment() {

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
        val sessionController = SessionController(
            currentWorkingDirectory = requireContext().filesDir.absolutePath,
            environment = systemEnvironment().apply {
                put("HOME", requireContext().filesDir.absolutePath)
            }
        )
        terminalView.let {
            it.setBackgroundColor(0xFF212121.toInt())
            val sessionCallback = TermSessionCallback(it)
            val session = sessionController.create(sessionCallback)
            val viewClient = TermViewClient(requireContext(), it, session)
            it.setEnableWordBasedIme(false)
            it.setTerminalViewClient(viewClient)
            it.attachSession(session)
            it.requestFocus()
        }
    }


}