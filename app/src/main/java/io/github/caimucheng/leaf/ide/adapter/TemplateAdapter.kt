package io.github.caimucheng.leaf.ide.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.ide.databinding.LayoutTemplateBinding
import io.github.caimucheng.leaf.ide.model.Plugin

class TemplateAdapter(
    private val context: Context,
    private val plugins: List<Plugin>,
    private val onItemClick: (plugin: Plugin) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {

    inner class ViewHolder(val viewBinding: LayoutTemplateBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutTemplateBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return plugins.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewBinding = holder.viewBinding
        val plugin = plugins[position]

        viewBinding.icon.background = plugin.pluginAPP.getTemplateIcon()
        viewBinding.title.text = plugin.pluginAPP.getTemplateTitle()
        viewBinding.root.setOnClickListener {
            onItemClick(plugin)
        }
    }

}