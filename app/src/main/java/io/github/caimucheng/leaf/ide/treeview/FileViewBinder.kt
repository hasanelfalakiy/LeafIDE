package io.github.caimucheng.leaf.ide.treeview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.Space
import androidx.core.view.updateLayoutParams
import com.google.android.material.checkbox.MaterialCheckBox
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FileTreeItemDirBinding
import io.github.caimucheng.leaf.ide.databinding.FileTreeItemFileBinding
import io.github.dingyi222666.view.treeview.TreeNode
import io.github.dingyi222666.view.treeview.TreeNodeEventListener
import io.github.dingyi222666.view.treeview.TreeView
import io.github.dingyi222666.view.treeview.TreeViewBinder
import java.io.File

class FileViewBinder : TreeViewBinder<File>(), TreeNodeEventListener<File> {
    private var onItemClick: ((File?) -> Unit)? = null
    private var onLongItemClick: ((File?) -> Boolean)? = null

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
        }
        onItemClick?.let { it(node.data) }
    }

    override fun onLongClick(node: TreeNode<File>, holder: TreeView.ViewHolder): Boolean {
        return if (onLongItemClick == null) {
            super.onLongClick(node, holder)
        } else {
            onLongItemClick!!(node.data)
        }
    }

    override fun onToggle(
        node: TreeNode<File>,
        isExpand: Boolean,
        holder: TreeView.ViewHolder
    ) {
        applyDir(holder, node)
    }

    fun setOnItemClickListener(listener: (File?) -> Unit) {
        onItemClick = listener
    }

    fun setOnLongItemClickListener(listener: (File?) -> Boolean) {
        onLongItemClick = listener
    }
}