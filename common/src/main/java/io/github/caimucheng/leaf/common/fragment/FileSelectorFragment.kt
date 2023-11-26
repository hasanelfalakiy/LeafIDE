package io.github.caimucheng.leaf.common.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.caimucheng.leaf.common.R
import io.github.caimucheng.leaf.common.adapter.FileSelectorAdapter
import io.github.caimucheng.leaf.common.callback.FileSelectorCallback
import io.github.caimucheng.leaf.common.databinding.FragmentFileSelectorBinding
import io.github.caimucheng.leaf.common.viewmodel.FileSelectorIntent
import io.github.caimucheng.leaf.common.viewmodel.FileSelectorViewModel
import io.github.caimucheng.leaf.common.viewmodel.FileState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

class FileSelectorFragment : DialogFragment() {

    private lateinit var viewBinding: FragmentFileSelectorBinding

    private var fileSelectorCallback: FileSelectorCallback? = null

    private val files by lazy {
        ArrayList<File>()
    }

    private val adapter by lazy {
        FileSelectorAdapter(
            context = requireContext(),
            files = files,
            matchingSuffix = arguments?.getStringArrayList("matchingSuffix"),
            onItemClick = {
                if (it.isDirectory) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        fileSelectorViewModel.intent.send(
                            FileSelectorIntent.Enter(it, arguments?.getStringArrayList("matchingSuffix"))
                        )
                    }
                } else {
                    fileSelectorCallback?.onFileSelected(it)
                }
            }
        )
    }

    private val fileSelectorViewModel: FileSelectorViewModel by viewModels()

    fun setFileSelectorCallback(fileSelectorCallback: FileSelectorCallback) {
        this.fileSelectorCallback = fileSelectorCallback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), theme)
            .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentFileSelectorBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                fileSelectorViewModel.intent.send(FileSelectorIntent.Refresh(arguments?.getStringArrayList("matchingSuffix")))
                fileSelectorViewModel.state.collectLatest {
                    when (it.fileState) {
                        FileState.Loading -> {
                            viewBinding.content.visibility = View.GONE
                            viewBinding.placeholder.visibility = View.GONE
                            viewBinding.loading.visibility = View.VISIBLE
                        }

                        FileState.Done -> {
                            viewBinding.loading.visibility = View.GONE
                            viewBinding.toolbar.subtitle = it.currentDirectory.absolutePath
                            files.clear()
                            files.addAll(it.files)
                            adapter.notifyDataSetChanged()
                            if (files.isNotEmpty()) {
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
        viewBinding.toolbar.setNavigationOnClickListener {
            dismiss()
        }
        viewBinding.toolbar.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_file_selector, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.up) {
                    if (fileSelectorViewModel.state.value.currentDirectory.absolutePath != Environment.getExternalStorageDirectory().absolutePath) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            fileSelectorViewModel.intent.send(
                                FileSelectorIntent.Enter(
                                    fileSelectorViewModel.state.value.currentDirectory.parentFile
                                        ?: Environment.getExternalStorageDirectory(),
                                    arguments?.getStringArrayList("matchingSuffix")
                                )
                            )
                        }
                    }
                }
                return false
            }
        }, viewLifecycleOwner)
    }

    private fun setupRecyclerView() {
        viewBinding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        viewBinding.recyclerView.adapter = adapter
    }

}