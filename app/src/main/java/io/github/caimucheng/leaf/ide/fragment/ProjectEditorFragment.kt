package io.github.caimucheng.leaf.ide.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import io.github.caimucheng.leaf.common.util.ViewUtils
import io.github.caimucheng.leaf.common.util.showDialog
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentProjectEditorBinding
import io.github.caimucheng.leaf.ide.treeview.FileListLoader
import io.github.caimucheng.leaf.ide.treeview.FileViewBinder
import io.github.caimucheng.leaf.ide.util.FileNodeGenerator
import io.github.caimucheng.leaf.ide.viewmodel.ProjectEditorIntent
import io.github.caimucheng.leaf.ide.viewmodel.ProjectEditorViewModel
import io.github.caimucheng.leaf.ide.viewmodel.ProjectState
import io.github.dingyi222666.view.treeview.Tree
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProjectEditorFragment : Fragment() {
    private lateinit var viewBinding: FragmentProjectEditorBinding
    private var fileListLoader: FileListLoader? = null
    private var fileTree: Tree<File>? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                viewBinding.drawerLayout.closeDrawers()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    ProjectEditorViewModel.intent.send(ProjectEditorIntent.CloseProject)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentProjectEditorBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val projectPath = requireArguments().getString("projectPath")

        setupToolbar()
        setupPlaceHolder()
        setupFileTreeView()

        requireActivity().onBackPressedDispatcher.addCallback(
            owner = viewLifecycleOwner,
            onBackPressedCallback = onBackPressedCallback
        )

        viewLifecycleOwner.lifecycleScope.launch {
            ProjectEditorViewModel.intent.send(ProjectEditorIntent.OpenProject(projectPath))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ProjectEditorViewModel.state.collectLatest {
                when (it.projectState) {
                    ProjectState.Exit -> {
                        findNavController().popBackStack()
                    }

                    ProjectState.Free -> {
                        ViewUtils.goneView(
                            viewBinding.editorContent,
                            viewBinding.loading,
                            viewBinding.placeholder
                        )
                    }

                    ProjectState.Loading -> {
                        ViewUtils.visibilityView(
                            viewBinding.loading
                        )
                    }

                    ProjectState.Error -> {
                        requireContext().showDialog(
                            cancelable = false,
                            titleResId = R.string.open_project_fail,
                            messageResId = R.string.open_project_fail_message,
                            positiveTextResId = R.string.close_project,
                            positiveEvent = { _, _ -> findNavController().popBackStack() }
                        )
                    }

                    ProjectState.Loaded -> {
                        ViewUtils.goneView(viewBinding.loading)
                        ViewUtils.visibilityView(viewBinding.placeholder)
                        it.project?.let { project ->
                            viewBinding.toolbar.title = project.name
                            refreshTreeView(project.projectPath)
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = viewBinding.toolbar
        val drawerLayout = viewBinding.drawerLayout
        val drawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            toolbar,
            R.string.drawerlayout_expand,
            R.string.drawerlayout_shrink
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.close_project -> {
                    if (view != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            ProjectEditorViewModel.intent.send(ProjectEditorIntent.CloseProject)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
    }

    private fun setupPlaceHolder() {
        viewBinding.openFileList.let {
            it.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            it.paint.isAntiAlias = true
            it.setOnClickListener {
                viewBinding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        viewBinding.openOptionsMenu.let {
            it.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            it.paint.isAntiAlias = true
            it.setOnClickListener {
                viewBinding.toolbar.showOverflowMenu()
            }
        }
    }

    private fun setupFileTreeView() {
        val treeView = viewBinding.treeView
        val binder = FileViewBinder()

        binder.setOnItemClickListener { file ->
            file?.let {
                Toast.makeText(
                    requireContext(),
                    "click ${file.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binder.setOnLongItemClickListener { file ->
            file?.let {
                Toast.makeText(
                    requireContext(),
                    "long click ${file.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }

        treeView.bindCoroutineScope(lifecycleScope)
        treeView.binder = binder
        treeView.nodeEventListener = treeView.binder as FileViewBinder
        fileListLoader = FileListLoader()
    }

    private fun createTree(rootPath: String): Tree<File> {
        val tree = Tree.createTree<File>()
        tree.apply {
            this.generator = FileNodeGenerator(
                File(rootPath),
                fileListLoader!!
            )
            initTree()
        }
        return tree
    }

    private fun refreshTreeView(projectPath: String?) {
        if (projectPath.isNullOrBlank()) return
        lifecycleScope.launch {
            fileTree = withContext(Dispatchers.Default) {
                fileListLoader!!.loadFileList(projectPath)
                createTree(projectPath)
            }
            viewBinding.treeView.tree = fileTree!!
            viewBinding.treeView.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }
}