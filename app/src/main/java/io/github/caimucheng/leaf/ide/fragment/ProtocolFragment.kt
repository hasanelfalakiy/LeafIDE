package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.databinding.FragmentProtocolBinding
import io.github.caimucheng.leaf.ide.util.findGlobalNavController
import io.github.caimucheng.leaf.ide.util.getTextFromAssets
import io.github.caimucheng.leaf.ide.util.language
import io.github.caimucheng.leaf.ide.util.parseProtocol

class ProtocolFragment : Fragment() {
    private lateinit var viewBinding: FragmentProtocolBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentProtocolBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val protocolType = requireArguments().getString("protocolType") ?: "PrivacyPolicy"
        setupToolbar(protocolType)
        setupContent(protocolType)
    }

    private fun setupToolbar(protocolType: String) {
        if (protocolType == "PrivacyPolicy") {
            viewBinding.toolbar.setTitle(R.string.privacy_policy)
        } else {
            viewBinding.toolbar.setTitle(R.string.user_agreement)
        }

        viewBinding.toolbar.setNavigationOnClickListener {
            findGlobalNavController().popBackStack()
        }
    }

    private fun setupContent(protocolType: String) {
        val language = AppContext.current.language
        viewBinding.content.text = parseProtocol(
            requireContext().getTextFromAssets("protocol/${language}/$protocolType.txt")
        )
    }
}