package io.github.caimucheng.leaf.ide.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentAboutBinding
import io.github.caimucheng.leaf.ide.util.findGlobalNavController
import io.github.caimucheng.leaf.ide.util.getVersionNameSelf
import io.github.caimucheng.leaf.ide.util.openWebPage

class AboutFragment : Fragment() {
    private lateinit var viewBinding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentAboutBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupFooter()
    }

    private fun setupToolbar() {
        viewBinding.toolbar.setNavigationOnClickListener {
            findGlobalNavController().popBackStack()
        }

        viewBinding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.project_repo) {
                openWebPage(requireContext(), "https://github.com/CaiMuCheng/LeafIDE")
                return@setOnMenuItemClickListener true
            } else {
                return@setOnMenuItemClickListener false
            }
        }
    }

    private fun setupFooter() {
        viewBinding.footer.text = String.format(
            "%s v%s(%s)",
            getString(R.string.app_name),
            getVersionNameSelf(requireContext()),
            Build.CPU_ABI
        )
    }
}