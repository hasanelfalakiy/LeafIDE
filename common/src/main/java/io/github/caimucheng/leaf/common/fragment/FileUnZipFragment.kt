package io.github.caimucheng.leaf.common.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.caimucheng.leaf.common.R
import io.github.caimucheng.leaf.common.callback.FileUnZipCallback
import io.github.caimucheng.leaf.common.databinding.FragmentFileUnzipBinding
import io.github.caimucheng.leaf.common.viewmodel.FileUnZipIntent
import io.github.caimucheng.leaf.common.viewmodel.FileUnZipTotalState
import io.github.caimucheng.leaf.common.viewmodel.FileUnZipViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class FileUnZipFragment : DialogFragment() {

    private lateinit var viewBinding: FragmentFileUnzipBinding

    private val fileUnZipViewModel: FileUnZipViewModel by viewModels()

    private var fileUnZipCallback: FileUnZipCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentFileUnzipBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    fun setFileUnZipCallback(fileUnZipCallback: FileUnZipCallback) {
        this.fileUnZipCallback = fileUnZipCallback
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val from = arguments?.getString("from") ?: return dismiss()
        val to = arguments?.getString("to") ?: return dismiss()

        viewLifecycleOwner.lifecycleScope.launch {
            if (fileUnZipViewModel.state.value.totalState == FileUnZipTotalState.UNSTARTED) {
                fileUnZipViewModel.intent.send(FileUnZipIntent.Start(from, to))
            }

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fileUnZipViewModel.state.collectLatest {
                    val fromFile = File(it.from)
                    val toFile = File(it.to)

                    viewBinding.name.text = getString(R.string.name, fromFile.name)
                    viewBinding.from.text = getString(R.string.from, fromFile.absolutePath)
                    viewBinding.to.text = getString(R.string.to, toFile.absolutePath)
                    viewBinding.progress.text = getString(R.string.progress, it.progress) + "%"
                    viewBinding.indicator.progress = it.progress
                    if (it.totalState == FileUnZipTotalState.DONE) {
                        fileUnZipCallback?.onUnZipSuccess() ?: dismiss()
                    }
                    if (it.totalState == FileUnZipTotalState.FAILED) {
                        fileUnZipCallback?.onUnZipFailed(it.exception!!) ?: dismiss()
                    }
                }
            }
        }
    }

}