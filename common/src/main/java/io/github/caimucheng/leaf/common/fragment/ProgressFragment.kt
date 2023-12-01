package io.github.caimucheng.leaf.common.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.caimucheng.leaf.common.databinding.FragmentProgressBinding

class ProgressFragment : DialogFragment() {

    private var title = ""

    private var message = ""

    private var max = 100

    private var progress = 0

    private var type = "circle"

    private lateinit var viewBinding: FragmentProgressBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .show()
    }

    fun setTitle(title: String): ProgressFragment {
        this.title = title
        if (::viewBinding.isInitialized) {
            viewBinding.toolbar.title = title
        }
        return this
    }

    fun setMessage(message: String): ProgressFragment {
        this.message = message
        if (::viewBinding.isInitialized) {
            when (type) {
                "linear" -> viewBinding.linearProgressMessage.text = message
                "circle" -> viewBinding.circleProgressMessage.text = message
            }
        }
        return this
    }

    @SuppressLint("SetTextI18n")
    fun setMax(max: Int): ProgressFragment {
        this.max = max
        if (::viewBinding.isInitialized) {
            when (type) {
                "linear" -> {
                    viewBinding.linearProgressIndicator.max = max
                    viewBinding.percent.text = "${progress / max}%"
                }

                "circle" -> {
                    viewBinding.circleProgressIndicator.max = max
                }
            }
        }
        return this
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int) {
        this.progress = progress
        if (::viewBinding.isInitialized) {
            when (type) {
                "linear" -> {
                    viewBinding.linearProgressIndicator.progress = progress
                    viewBinding.percent.text = "${progress / max}%"
                }

                "circle" -> {
                    viewBinding.circleProgressIndicator.progress = progress
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentProgressBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        type = arguments?.getString("type", "circle") ?: "circle"
        val indeterminate = arguments?.getBoolean("indeterminate", true) ?: true
        when (type) {
            "circle" -> viewBinding.circleProgressContent.visibility = View.VISIBLE
            "linear" -> viewBinding.linearProgressContent.visibility = View.VISIBLE
            else -> throw IllegalArgumentException("Unknown type")
        }

        viewBinding.toolbar.title = title
        when (type) {
            "linear" -> {
                viewBinding.linearProgressIndicator.isIndeterminate = indeterminate
                viewBinding.linearProgressMessage.text = message
                viewBinding.linearProgressIndicator.max = max
                viewBinding.linearProgressIndicator.progress = progress
                viewBinding.percent.text = "${progress / max}%"
            }

            "circle" -> {
                viewBinding.circleProgressIndicator.isIndeterminate = indeterminate
                viewBinding.circleProgressMessage.text = message
                viewBinding.circleProgressIndicator.max = max
                viewBinding.circleProgressIndicator.progress = progress
            }
        }
    }

}