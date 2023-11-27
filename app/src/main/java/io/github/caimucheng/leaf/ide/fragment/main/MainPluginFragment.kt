package io.github.caimucheng.leaf.ide.fragment.main

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.common.callback.FileCopyCallback
import io.github.caimucheng.leaf.common.callback.FileSelectorCallback
import io.github.caimucheng.leaf.common.fragment.FileCopyFragment
import io.github.caimucheng.leaf.common.fragment.FileSelectorFragment
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.adapter.MainPluginAdapter
import io.github.caimucheng.leaf.ide.databinding.FragmentMainPluginBinding
import io.github.caimucheng.leaf.ide.model.Plugin
import io.github.caimucheng.leaf.ide.model.name
import io.github.caimucheng.leaf.ide.util.LeafIDEPluginRootPath
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel
import io.github.caimucheng.leaf.ide.viewmodel.PluginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MainPluginFragment : Fragment() {

    private lateinit var viewBinding: FragmentMainPluginBinding

    private val plugins by lazy {
        ArrayList<Plugin>(AppViewModel.state.value.plugins)
    }

    private val adapter by lazy {
        MainPluginAdapter(
            context = requireContext(),
            plugins = plugins,
            onToggle = {
                viewLifecycleOwner.lifecycleScope.launch {
                    AppViewModel.intent.send(AppIntent.Refresh)
                }
            },
            onUninstall = {
                showUninstallDialog(it)
            }
        )
    }

    private fun showUninstallDialog(plugin: Plugin) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(plugin.name)
            .setIcon(plugin.icon)
            .setMessage(R.string.uninstall_plugin)
            .setNeutralButton(R.string.cancel, null)
            .setPositiveButton(R.string.sure) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    AppViewModel.intent.send(
                        AppIntent.Uninstall(
                            plugin.packageName,
                            requireContext(),
                            childFragmentManager
                        )
                    )
                }
            }.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentMainPluginBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppViewModel.state.collectLatest {
                    when (it.pluginState) {
                        PluginState.Loading -> {
                            viewBinding.content.visibility = View.GONE
                            viewBinding.placeholder.visibility = View.GONE
                            viewBinding.loading.visibility = View.VISIBLE
                        }

                        PluginState.Done -> {
                            viewBinding.loading.visibility = View.GONE
                            plugins.clear()
                            plugins.addAll(it.plugins)
                            adapter.notifyDataSetChanged()
                            if (plugins.isNotEmpty()) {
                                viewBinding.placeholder.visibility = View.GONE
                                viewBinding.content.visibility = View.VISIBLE
                            } else {
                                viewBinding.content.visibility = View.GONE
                                viewBinding.placeholder.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        viewBinding.toolbar.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main_plugin, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.installFromLocal) {
                    showFileSelector()
                }
                return false
            }

        }, viewLifecycleOwner)
    }

    private fun showFileSelector() {
        val fileSelectorFragment = FileSelectorFragment()
        fileSelectorFragment.arguments = bundleOf(
            "matchingSuffix" to arrayListOf(".apk")
        )
        fileSelectorFragment.setFileSelectorCallback(object : FileSelectorCallback {

            override fun onFileSelected(file: File) {
                fileSelectorFragment.dismiss()
                parsePluginDialog(file)
            }

        })
        fileSelectorFragment.show(childFragmentManager, "installFromLocal")
    }

    private fun parsePluginDialog(file: File) {
        val packageManager = requireContext().packageManager
        val packageInfo = packageManager.getPackageArchiveInfo(
            file.absolutePath, PackageManager.GET_META_DATA
        )
        if (packageInfo != null) {
            val application = packageInfo.applicationInfo
            application.sourceDir = file.absolutePath
            application.publicSourceDir = file.absolutePath

            val packageName = application.packageName
            showFileCopyDialog(file, packageName)
        } else {
            Toasty.error(requireContext(), R.string.unable_to_resolve_plugin, Toasty.LENGTH_LONG)
                .show()
        }
    }

    private fun showFileCopyDialog(file: File, packageName: String) {
        val fileCopyFragment = FileCopyFragment()
        fileCopyFragment.isCancelable = false
        fileCopyFragment.arguments = bundleOf(
            "name" to file.name,
            "from" to file.absolutePath,
            "to" to File(LeafIDEPluginRootPath, "${packageName}.apk").absolutePath,
        )
        fileCopyFragment.setFileCopyCallback(object : FileCopyCallback {
            override fun onCopySuccess() {
                fileCopyFragment.dismiss()
                Toasty.success(requireContext(), R.string.copy_success, Toasty.LENGTH_SHORT)
                    .show()
                installPlugin(packageName)
            }

            override fun onCopyFailed(e: Exception) {
                fileCopyFragment.dismiss()
                Toasty.error(
                    requireContext(),
                    getString(R.string.copy_failure, e.message),
                    Toasty.LENGTH_LONG
                ).show()
            }
        })
        fileCopyFragment.show(childFragmentManager, "fileCopy")
    }

    private fun installPlugin(packageName: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val plugin = withContext(Dispatchers.IO) {
                AppViewModel.state.value.plugins.find { it.packageName == packageName }
            }
            if (plugin != null) {
                AppViewModel.intent.send(
                    AppIntent.Update(
                        packageName,
                        requireActivity(),
                        childFragmentManager
                    )
                )
            } else {
                AppViewModel.intent.send(
                    AppIntent.Install(
                        packageName,
                        requireActivity(),
                        childFragmentManager
                    )
                )
            }
        }
    }

    private fun setupRecyclerView() {
        viewBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.recyclerView.adapter = adapter
    }

}