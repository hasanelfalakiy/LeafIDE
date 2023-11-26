package io.github.caimucheng.leaf.ide.fragment.main

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.common.callback.FileCopyCallback
import io.github.caimucheng.leaf.common.fragment.FileCopyFragment
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentMainSettingsBinding
import io.github.caimucheng.leaf.ide.util.LeafIDEPluginRootPath
import java.io.File

class MainSettingsFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMainSettingsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}