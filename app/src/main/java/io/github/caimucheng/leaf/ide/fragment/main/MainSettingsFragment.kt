package io.github.caimucheng.leaf.ide.fragment.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.common.callback.FileUnZipCallback
import io.github.caimucheng.leaf.common.fragment.FileUnZipFragment
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentMainSettingsBinding
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
        val fileUnZipFragment = FileUnZipFragment()
        fileUnZipFragment.isCancelable = false
        fileUnZipFragment.arguments = bundleOf(
            "name" to "nodejs.zip",
            "from" to "nodejs.zip",
            "to" to File(requireContext().filesDir, "isolate").absolutePath,
            "type" to "zip"
        )
        fileUnZipFragment.setAssets(requireActivity().assets)
        fileUnZipFragment.setFileUnZipCallback(object : FileUnZipCallback {
            override fun onUnZipSuccess() {
                fileUnZipFragment.dismiss()
                Toasty.success(requireContext(), R.string.copy_success, Toasty.LENGTH_SHORT)
                    .show()
            }

            override fun onUnZipFailed(e: Exception) {
                fileUnZipFragment.dismiss()
                e.printStackTrace()
                Toasty.error(
                    requireContext(),
                    getString(R.string.copy_failure, e.message),
                    Toasty.LENGTH_LONG
                ).show()
            }
        })
        fileUnZipFragment.show(childFragmentManager, "fileCopy")
    }

}