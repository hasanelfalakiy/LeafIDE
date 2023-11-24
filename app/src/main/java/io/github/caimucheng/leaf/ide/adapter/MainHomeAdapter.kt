package io.github.caimucheng.leaf.ide.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.LayoutMainHomeBinding
import io.github.caimucheng.leaf.ide.model.Project

class MainHomeAdapter(
    private val context: Context,
    private val projects: List<Project>
) : RecyclerView.Adapter<MainHomeAdapter.ViewHolder>() {

    private val inflater by lazy {
        LayoutInflater.from(context)
    }

    inner class ViewHolder(val viewBinding: LayoutMainHomeBinding) : RecyclerView.ViewHolder(viewBinding.root)

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
        viewBinding.projectDescription.text = context.getString(R.string.project_description, project.description)
        viewBinding.pluginSupport.text = context.getString(R.string.plugin_support, project.plugin.packageName)
        viewBinding.icon.background = project.plugin.pluginAPP.getSmallIcon()
        viewBinding.subscript.text = project.plugin.pluginAPP.getSubscript()
    }

}