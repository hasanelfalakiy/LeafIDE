package io.github.caimucheng.leaf.ide.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import com.chad.library.adapter4.BaseQuickAdapter
import com.chad.library.adapter4.viewholder.QuickViewHolder
import com.google.android.material.textview.MaterialTextView
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.model.TreeNode
import java.io.File

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