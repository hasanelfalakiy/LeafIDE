package io.github.caimucheng.leaf.ide.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.LayoutMainHomeBinding
import io.github.caimucheng.leaf.ide.model.Project
import io.github.caimucheng.leaf.ide.model.moduleSupport

class MainHomeAdapter(
    private val context: Context,
    private val projects: List<Project>
) : RecyclerView.Adapter<MainHomeAdapter.ViewHolder>() {

    private val inflater by lazy {
        LayoutInflater.from(context)
    }

    private var onItemClickListener: ((View, Int) -> Unit)? = null

    private var onItemLongClickListener: ((View, Int) -> Unit)? = null

    inner class ViewHolder(val viewBinding: LayoutMainHomeBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutMainHomeBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return projects.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        val viewBinding = holder.viewBinding
        viewBinding.projectName.text = project.name
        viewBinding.projectDescription.text = context.getString(
            R.string.project_description,
            project.description
        )
        viewBinding.moduleSupport.text = context.getString(
            R.string.module_support,
            project.module.moduleSupport
        )
        viewBinding.icon.background = project.module.moduleAPP.getProjectCardIcon()
        viewBinding.subscript.text = project.module.moduleAPP.getProjectCardSubscript()

        if (onItemClickListener != null) {
            viewBinding.root.setOnClickListener {
                onItemClickListener!!(it, position)
            }
        }

        if (onItemLongClickListener != null) {
            viewBinding.root.setOnLongClickListener {
                onItemLongClickListener!!(it, position)
                true
            }
        }
    }

    fun setOnItemClickListener(listener: (View, Int) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: (View, Int) -> Unit) {
        onItemLongClickListener = listener
    }
}