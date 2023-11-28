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
import io.github.caimucheng.leaf.common.callback.FileDeleteCallback
import io.github.caimucheng.leaf.common.databinding.FragmentFileDeleteBinding
import io.github.caimucheng.leaf.common.viewmodel.FileDeleteIntent
import io.github.caimucheng.leaf.common.viewmodel.FileDeleteTotalState
import io.github.caimucheng.leaf.common.viewmodel.FileDeleteViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FileDeleteFragment : DialogFragment() {

    private lateinit var viewBinding: FragmentFileDeleteBinding

    private val fileDeleteViewModel: FileDeleteViewModel by viewModels()

    private var fileDeleteCallback: FileDeleteCallback? = null

    fun setFileDeleteCallback(fileDeleteCallback: FileDeleteCallback) {
        this.fileDeleteCallback = fileDeleteCallback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentFileDeleteBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString("name") ?: return dismiss()
        val path = arguments?.getString("path") ?: return dismiss()

        viewLifecycleOwner.lifecycleScope.launch {
            if (fileDeleteViewModel.state.value.totalState == FileDeleteTotalState.UNSTARTED) {
                fileDeleteViewModel.intent.send(FileDeleteIntent.Start(name, path))
            }

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fileDeleteViewModel.state.collectLatest {
                    viewBinding.name.text = getString(R.string.name, name)
                    viewBinding.path.text = getString(R.string.path, it.path)
                    if (it.totalState == FileDeleteTotalState.DONE) {
                        fileDeleteCallback?.onDeleteSuccess() ?: dismiss()
                    }
                    if (it.totalState == FileDeleteTotalState.FAILED) {
                        fileDeleteCallback?.onDeleteFailed() ?: dismiss()
                    }
                }
            }
        }
    }

}