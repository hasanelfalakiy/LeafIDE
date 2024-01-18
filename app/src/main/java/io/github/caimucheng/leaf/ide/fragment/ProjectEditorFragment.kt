package io.github.caimucheng.leaf.ide.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.Space
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.checkbox.MaterialCheckBox
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.common.util.ViewUtils
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FileTreeItemDirBinding
import io.github.caimucheng.leaf.ide.databinding.FileTreeItemFileBinding
import io.github.caimucheng.leaf.ide.databinding.FragmentProjectEditorBinding
import io.github.caimucheng.leaf.ide.loader.FileListLoader
import io.github.caimucheng.leaf.ide.util.FileNodeGenerator
import io.github.caimucheng.leaf.ide.util.findGlobalNavController
import io.github.caimucheng.leaf.ide.viewmodel.ProjectEditorIntent
import io.github.caimucheng.leaf.ide.viewmodel.ProjectEditorViewModel
import io.github.caimucheng.leaf.ide.viewmodel.ProjectStatus
import io.github.dingyi222666.view.treeview.Tree
import io.github.dingyi222666.view.treeview.TreeNode
import io.github.dingyi222666.view.treeview.TreeNodeEventListener
import io.github.dingyi222666.view.treeview.TreeView
import io.github.dingyi222666.view.treeview.TreeViewBinder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ProjectEditorFragment : Fragment() {
    private lateinit var viewBinding: FragmentProjectEditorBinding

    private var undoMenuItem: MenuItem? = null
    private var redoMenuItem: MenuItem? = null
    private var saveMenuItem: MenuItem? = null
    private var searchInsideFileMenuItem: MenuItem? = null
    private var fileListLoader: FileListLoader? = null
    private var tree: Tree<File>? = null

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
        if (projectPath.isNullOrBlank()) {
            findGlobalNavController().popBackStack()
        }

        setupToolbar()
        setupPlaceHolder()
        setupEditor()
        setupFileTreeView()
        setupFooter()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        viewLifecycleOwner.lifecycleScope.launch {
            ProjectEditorViewModel.intent.send(
                ProjectEditorIntent.OpenProject(
                    projectPath
                )
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ProjectEditorViewModel.state.collectLatest {
                when (it.projectStatus) {
                    ProjectStatus.CLEAR -> {

                    }

                    ProjectStatus.FREE -> {
                        ViewUtils.disableMenuItem(
                            undoMenuItem,
                            redoMenuItem,
                            saveMenuItem,
                            searchInsideFileMenuItem
                        )
                        ViewUtils.goneView(
                            viewBinding.loading,
                            viewBinding.placeholder,
                            viewBinding.editor
                        )
                    }

                    ProjectStatus.LOADING -> {
                        ViewUtils.disableMenuItem(
                            undoMenuItem,
                            redoMenuItem,
                            saveMenuItem,
                            searchInsideFileMenuItem
                        )
                        ViewUtils.visibilityView(viewBinding.loading)
                        ViewUtils.goneView(
                            viewBinding.placeholder,
                            viewBinding.editor
                        )
                    }

                    ProjectStatus.ERROR -> {
                        Toasty.error(requireContext(), R.string.error_opening_project, Toasty.LENGTH_LONG)
                            .show()
                        findGlobalNavController().popBackStack()
                    }

                    ProjectStatus.DONE -> {
                        ViewUtils.visibilityView(viewBinding.placeholder)
                        ViewUtils.goneView(
                            viewBinding.loading,
                            viewBinding.editor
                        )
                        if (it.project != null) {
                            viewBinding.toolbar.title = it.project.name
                        }
                        if (it.editorCurrentContent != null) {
                            ViewUtils.enableMenuItem(
                                undoMenuItem,
                                redoMenuItem,
                                saveMenuItem,
                                searchInsideFileMenuItem
                            )
                        } else {
                            ViewUtils.disableMenuItem(
                                undoMenuItem,
                                redoMenuItem,
                                saveMenuItem,
                                searchInsideFileMenuItem
                            )
                        }
                        refreshTreeView(it.project?.projectPath)
                    }

                    ProjectStatus.CLOSE -> {
                        ProjectEditorViewModel.intent.send(ProjectEditorIntent.Clear)
                        findGlobalNavController().popBackStack()
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

        undoMenuItem = toolbar.menu.findItem(R.id.undo)
        redoMenuItem = toolbar.menu.findItem(R.id.redo)
        saveMenuItem = toolbar.menu.findItem(R.id.save)
        searchInsideFileMenuItem = toolbar.menu.findItem(R.id.search_inside_file)

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
        viewBinding.openFileList.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        viewBinding.openFileList.paint.isAntiAlias = true

        viewBinding.openOptionsMenu.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        viewBinding.openOptionsMenu.paint.isAntiAlias = true

        viewBinding.openFileList.setOnClickListener {
            viewBinding.drawerLayout.openDrawer(GravityCompat.START)
        }

        viewBinding.openOptionsMenu.setOnClickListener {
            viewBinding.toolbar.showOverflowMenu()
        }
    }

    private fun setupEditor() {

    }

    private fun setupFileTreeView() {
        val treeView = viewBinding.treeView
        treeView.bindCoroutineScope(lifecycleScope)
        treeView.binder = FileViewBinder()
        treeView.nodeEventListener = treeView.binder as FileViewBinder
        fileListLoader = FileListLoader()
    }

    private fun setupFooter() {
        val symbolInputView = viewBinding.symbolInputView
        symbolInputView.addSymbols(
            arrayOf(
                "Tab", "{", "}", "(", ")", ",", ".", ";", "\"", "?",
                "+", "-", "*", "/", "<", ">", "[", "]", ":"
            ),
            arrayOf(
                "\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?",
                "+", "-", "*", "/", "<", ">", "[", "]", ":"
            )
        )
        symbolInputView.bindEditor(viewBinding.editor)
    }

    private fun refreshTreeView(projectPath: String?) {
        if (projectPath.isNullOrBlank()) return
        lifecycleScope.launch {
            tree = withContext(Dispatchers.Default) {
                fileListLoader!!.loadFileList(projectPath)
                createTree(projectPath)
            }
            viewBinding.treeView.tree = tree!!
            viewBinding.treeView.refresh()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
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

    inner class FileViewBinder : TreeViewBinder<File>(), TreeNodeEventListener<File> {
        override fun createView(parent: ViewGroup, viewType: Int): View {
            val layoutInflater = LayoutInflater.from(parent.context)
            return if (viewType == 1) {
                FileTreeItemDirBinding.inflate(layoutInflater, parent, false).root
            } else {
                FileTreeItemFileBinding.inflate(layoutInflater, parent, false).root
            }
        }

        override fun getItemViewType(node: TreeNode<File>): Int {
            return if (node.isChild) 1 else 0
        }

        override fun bindView(
            holder: TreeView.ViewHolder,
            node: TreeNode<File>,
            listener: TreeNodeEventListener<File>
        ) {
            if (node.isChild) {
                applyDir(holder, node)
            } else {
                applyFile(holder, node)
            }

            val itemView = holder.itemView.findViewById<Space>(R.id.space)

            itemView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                width = node.depth * 22
            }

            (getCheckableView(node, holder) as MaterialCheckBox).apply {
                visibility = if (node.selected) View.VISIBLE else View.GONE
                isSelected = node.selected
            }
        }

        private fun applyFile(holder: TreeView.ViewHolder, node: TreeNode<File>) {
            val binding = FileTreeItemFileBinding.bind(holder.itemView)
            binding.tvName.text = node.name.toString()
        }

        private fun applyDir(holder: TreeView.ViewHolder, node: TreeNode<File>) {
            val binding = FileTreeItemDirBinding.bind(holder.itemView)
            binding.tvName.text = node.name.toString()
            binding
                .ivArrow
                .animate()
                .rotation(if (node.expand) 90f else 0f)
                .setDuration(200)
                .start()
        }

        override fun getCheckableView(
            node: TreeNode<File>,
            holder: TreeView.ViewHolder
        ): Checkable {
            return if (node.isChild) {
                FileTreeItemDirBinding.bind(holder.itemView).checkbox
            } else {
                FileTreeItemFileBinding.bind(holder.itemView).checkbox
            }
        }

        override fun onClick(node: TreeNode<File>, holder: TreeView.ViewHolder) {
            if (node.isChild) {
                applyDir(holder, node)
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "Clicked ${node.name}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        override fun onToggle(
            node: TreeNode<File>,
            isExpand: Boolean,
            holder: TreeView.ViewHolder
        ) {
            applyDir(holder, node)
        }
    }
}