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
import io.github.caimucheng.leaf.common.callback.FileCopyCallback
import io.github.caimucheng.leaf.common.databinding.FragmentFileCopyBinding
import io.github.caimucheng.leaf.common.viewmodel.FileCopyIntent
import io.github.caimucheng.leaf.common.viewmodel.FileCopyTotalState
import io.github.caimucheng.leaf.common.viewmodel.FileCopyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class FileCopyFragment : DialogFragment() {

    private lateinit var viewBinding: FragmentFileCopyBinding

    private val fileCopyViewModel: FileCopyViewModel by viewModels()

    private var fileCopyCallback: FileCopyCallback? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentFileCopyBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    fun setFileCopyCallback(fileCopyCallback: FileCopyCallback) {
        this.fileCopyCallback = fileCopyCallback
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val from = arguments?.getString("from") ?: return dismiss()
        val to = arguments?.getString("to") ?: return dismiss()

        viewLifecycleOwner.lifecycleScope.launch {
            if (fileCopyViewModel.state.value.totalState == FileCopyTotalState.UNSTARTED) {
                fileCopyViewModel.intent.send(FileCopyIntent.Start(from, to))
            }

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fileCopyViewModel.state.collectLatest {
                    val fromFile = File(it.from)
                    val toFile = File(it.to)
                    if (fromFile.isFile) {
                        viewBinding.title.title = getString(R.string.copy_file)
                    } else {
                        viewBinding.title.title = getString(R.string.copy_folder)
                    }
                    viewBinding.name.text = getString(R.string.name, fromFile.name)
                    viewBinding.from.text = getString(R.string.from, fromFile.absolutePath)
                    viewBinding.to.text = getString(R.string.to, toFile.absolutePath)
                    viewBinding.progress.text = getString(R.string.progress, it.progress) + "%"
                    viewBinding.indicator.progress = it.progress
                    if (it.totalState == FileCopyTotalState.DONE) {
                        fileCopyCallback?.onCopySuccess() ?: dismiss()
                    }
                    if (it.totalState == FileCopyTotalState.FAILED) {
                        fileCopyCallback?.onCopyFailed(it.exception!!) ?: dismiss()
                    }
                }
            }
        }
    }

}