package io.github.caimucheng.leaf.ide.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.google.android.material.textview.MaterialTextView
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentEditorBinding
import io.github.caimucheng.leaf.ide.model.TreeNode
import io.github.caimucheng.leaf.ide.viewmodel.EditorAction
import io.github.caimucheng.leaf.ide.viewmodel.EditorIntent
import io.github.caimucheng.leaf.ide.viewmodel.EditorViewModel
import io.github.caimucheng.leaf.ide.viewmodel.FileTreeAction
import io.github.caimucheng.leaf.ide.viewmodel.FileTreeIntent
import io.github.caimucheng.leaf.ide.viewmodel.FileTreeViewModel
import io.github.rosemoe.sora.event.ContentChangeEvent
import io.github.rosemoe.sora.widget.subscribeEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File


class EditorFragment : Fragment() {
    private lateinit var viewBinding: FragmentEditorBinding
    private var undo: MenuItem? = null
    private var redo: MenuItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentEditorBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupEditor()
        setupFileTreeView()
        setupBottomBar()

        viewLifecycleOwner.lifecycleScope.launch {
            FileTreeViewModel.intent.send(FileTreeIntent.Open(requireContext().filesDir.parent!!))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            FileTreeViewModel.state.collectLatest {
                when (it.action) {
                    FileTreeAction.Free -> return@collectLatest

                    FileTreeAction.Done -> {
                        val adapter = FileTreeViewAdapter(it.list)
                        adapter.submitList(it.list)
                        viewBinding.fileTreeView.adapter = adapter
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            EditorViewModel.state.collectLatest {
                when (it.editorAction) {
                    EditorAction.SHOW -> {
                        viewBinding.editor.visibility = View.VISIBLE
                        viewBinding.bottomBar.visibility = View.VISIBLE
                        viewBinding.notOpenFileLayout.visibility = View.GONE
                        undo?.setVisible(true)
                        redo?.setVisible(true)
                    }

                    EditorAction.HIDE -> {
                        viewBinding.editor.visibility = View.GONE
                        viewBinding.bottomBar.visibility = View.GONE
                        viewBinding.notOpenFileLayout.visibility = View.VISIBLE
                        undo?.setVisible(true)
                        redo?.setVisible(true)
                    }

                    EditorAction.UPDATE -> {
                        undo?.setEnabled(it.canUndo)
                        redo?.setEnabled(it.canRedo)
                    }

                    EditorAction.UNDO -> viewBinding.editor.undo()

                    EditorAction.REDO -> viewBinding.editor.redo()

                    else -> return@collectLatest
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

        undo = toolbar.menu.findItem(R.id.undo)
        redo = toolbar.menu.findItem(R.id.redo)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.undo -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        EditorViewModel.intent.send(EditorIntent.Undo)
                    }
                    true
                }

                R.id.redo -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        EditorViewModel.intent.send(EditorIntent.Redo)
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun setupEditor() {
        val editor = viewBinding.editor

        editor.subscribeEvent<ContentChangeEvent> { _, _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                EditorViewModel.intent.send(
                    EditorIntent.RefreshCanUndoAndRedo(
                        canUndo = editor.canUndo(),
                        canRedo = editor.canRedo()
                    )
                )
            }
        }
    }

    private fun setupFileTreeView() {
        val fileTreeView = viewBinding.fileTreeView
        fileTreeView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupBottomBar() {
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

    class FileTreeViewAdapter(
        private val data: MutableList<TreeNode<File>>
    ) : BaseQuickAdapter<TreeNode<File>, QuickViewHolder>() {

        init {
            setOnItemClickListener { _, view, position ->
                val indicator = view.findViewById<AppCompatImageView>(R.id.indicator)
                val node = data[position]
                if (node.value.isDirectory && node.children.isNotEmpty()) {
                    if (node.expand) {
                        indicator.setImageResource(R.drawable.baseline_keyboard_arrow_right_24)
                        data.removeAll(shouldRemoveNode(node))
                        node.expand = false
                        notifyItemRangeRemoved(position + 1, calcChildrenSize(node))
                    } else {
                        indicator.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                        data.addAll(position + 1, node.children)
                        node.expand = true
                        notifyItemRangeInserted(position + 1, node.children.size)
                    }
                }
            }
        }

        override fun onBindViewHolder(
            holder: QuickViewHolder,
            position: Int,
            item: TreeNode<File>?
        ) {
            item?.let {
                val indicator = holder.getView<AppCompatImageView>(R.id.indicator)
                val icon = holder.getView<AppCompatImageView>(R.id.icon)
                val title = holder.getView<MaterialTextView>(R.id.title)
                val offset = 8
                val paddingLeft = 24 * (item.level - 1) + offset
                title.text = item.value.name
                if (item.value.isFile) {
                    holder.itemView.setPadding(paddingLeft, 0, offset, 0)
                    indicator.setImageBitmap(null)
                    icon.setImageResource(R.drawable.baseline_insert_drive_file_24)
                } else {
                    holder.itemView.setPadding(paddingLeft, 0, offset, 0)
                    if (item.value.listFiles().isNullOrEmpty()) {
                        indicator.setImageBitmap(null)
                    } else {
                        if (item.expand) {
                            indicator.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
                        } else {
                            indicator.setImageResource(R.drawable.baseline_keyboard_arrow_right_24)
                        }
                    }
                    icon.setImageResource(R.drawable.baseline_folder_24)
                }
            }
        }

        override fun onCreateViewHolder(
            context: Context,
            parent: ViewGroup,
            viewType: Int
        ): QuickViewHolder = QuickViewHolder(R.layout.file_tree_item, parent)

        private fun calcChildrenSize(node: TreeNode<File>): Int {
            if (!node.expand) {
                return 0
            }
            var size = 0
            for (child in node.children) {
                size += 1
                size += calcChildrenSize(child)
            }
            return size
        }

        private fun shouldRemoveNode(node: TreeNode<File>): MutableList<TreeNode<File>> {
            val list = mutableListOf<TreeNode<File>>()
            list.addAll(node.children)
            node.children.forEach {
                list.addAll(shouldRemoveNode(it))
            }
            return list
        }
    }
}