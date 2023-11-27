package io.github.caimucheng.leaf.common.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.AssetManager
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

    private var assets: AssetManager? = null

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

    fun setAssets(assets: AssetManager) {
        this.assets = assets
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString("name") ?: return dismiss()
        val from = arguments?.getString("from") ?: return dismiss()
        val to = arguments?.getString("to") ?: return dismiss()

        viewLifecycleOwner.lifecycleScope.launch {
            if (fileCopyViewModel.state.value.totalState == FileCopyTotalState.UNSTARTED) {
                if (assets != null) {
                    fileCopyViewModel.intent.send(
                        FileCopyIntent.StartFromAssets(
                            assets!!,
                            name,
                            from,
                            to
                        )
                    )
                } else {
                    fileCopyViewModel.intent.send(FileCopyIntent.Start(name, from, to))
                }
            }

            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fileCopyViewModel.state.collectLatest {
                    viewBinding.name.text = getString(R.string.name, name)
                    viewBinding.from.text = getString(R.string.from, it.from)
                    viewBinding.to.text = getString(R.string.to, it.to)
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