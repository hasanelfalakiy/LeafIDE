package leaf.nodejs.module.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.common.callback.FileDeleteCallback
import io.github.caimucheng.leaf.common.callback.FileSelectorCallback
import io.github.caimucheng.leaf.common.callback.FileUnZipCallback
import io.github.caimucheng.leaf.common.fragment.FileDeleteFragment
import io.github.caimucheng.leaf.common.fragment.FileSelectorFragment
import io.github.caimucheng.leaf.common.fragment.FileUnZipFragment
import io.github.caimucheng.leaf.module.fragment.ModuleFragment
import leaf.nodejs.module.R
import leaf.nodejs.module.databinding.FragmentManageNodejsModuleBinding
import java.io.File

class ManageNodeJSModuleFragment : ModuleFragment() {

    private lateinit var viewBinding: FragmentManageNodejsModuleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentManageNodejsModuleBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        val isolateDir = File(requireContext().filesDir, "isolate")
        val nodejsDir = File(isolateDir, "nodejs")
        if (nodejsDir.exists() && nodejsDir.isDirectory) {
            viewBinding.installPackageContent.visibility = View.GONE
            viewBinding.content.visibility = View.VISIBLE
        }

        viewBinding.toolbar.setNavigationOnClickListener {
            actionHolder.popBackStack()
        }

        viewBinding.installButton.setOnClickListener {
            val fileSelectorFragment = FileSelectorFragment()
            fileSelectorFragment.arguments = bundleOf(
                "matchingSuffix" to arrayListOf(".tgz")
            )
            fileSelectorFragment.setFileSelectorCallback(object : FileSelectorCallback {

                override fun onFileSelected(file: File) {
                    fileSelectorFragment.dismiss()
                    install(file)
                }

            })
            fileSelectorFragment.show(childFragmentManager, "installFromLocal")
        }

        viewBinding.uninstallPackage.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.uninstall_nodejs)
                .setMessage(R.string.uninstall_nodejs_summary)
                .setNeutralButton(R.string.cancel, null)
                .setPositiveButton(R.string.sure) { _, _ ->
                    val fileDeleteFragment = FileDeleteFragment()
                    fileDeleteFragment.arguments = bundleOf(
                        "name" to nodejsDir.name,
                        "path" to nodejsDir.absolutePath
                    )
                    fileDeleteFragment.isCancelable = false
                    fileDeleteFragment.setFileDeleteCallback(object : FileDeleteCallback {

                        override fun onDeleteSuccess() {
                            fileDeleteFragment.dismiss()
                            Toasty.success(
                                requireContext(),
                                R.string.uninstall_successfully,
                                Toasty.LENGTH_SHORT
                            ).show()
                            viewBinding.content.visibility = View.GONE
                            viewBinding.installPackageContent.visibility = View.VISIBLE
                        }

                        override fun onDeleteFailed() {
                            fileDeleteFragment.dismiss()
                            Toasty.error(
                                requireContext(),
                                R.string.uninstall_failed,
                                Toasty.LENGTH_LONG
                            ).show()
                        }

                    })
                    fileDeleteFragment.show(childFragmentManager, "uninstallPackage")
                }
                .show()
        }
    }

    private fun install(file: File) {
        val fileUnZipFragment = FileUnZipFragment()
        fileUnZipFragment.arguments = bundleOf(
            "name" to file.name,
            "from" to file.absolutePath,
            "to" to File(requireContext().filesDir, "isolate").absolutePath,
            "type" to "gz"
        )
        fileUnZipFragment.isCancelable = false
        fileUnZipFragment.setFileUnZipCallback(object : FileUnZipCallback {

            override fun onUnZipSuccess() {
                fileUnZipFragment.dismiss()
                Toasty.success(requireContext(), R.string.install_successfully, Toasty.LENGTH_SHORT)
                    .show()
                viewBinding.installPackageContent.visibility = View.GONE
                viewBinding.content.visibility = View.VISIBLE
            }

            override fun onUnZipFailed(e: Exception) {
                fileUnZipFragment.dismiss()
                Toasty.error(
                    requireContext(),
                    requireContext().getString(R.string.install_failed, e.message),
                    Toasty.LENGTH_LONG
                )
                    .show()
            }

        })
        fileUnZipFragment.show(childFragmentManager, "unzipPackage")
    }

}