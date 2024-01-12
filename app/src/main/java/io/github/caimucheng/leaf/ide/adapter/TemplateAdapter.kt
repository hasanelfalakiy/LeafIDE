package io.github.caimucheng.leaf.ide.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.ide.databinding.LayoutTemplateBinding
import io.github.caimucheng.leaf.ide.model.Module

class TemplateAdapter(
    private val context: Context,
    private val modules: List<Module>,
    private val onItemClick: (module: Module) -> Unit
) : RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {

    inner class ViewHolder(val viewBinding: LayoutTemplateBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutTemplateBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return modules.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewBinding = holder.viewBinding
        val module = modules[position]

        viewBinding.icon.background = module.moduleAPP.getTemplateIcon()
        viewBinding.title.text = module.moduleAPP.getTemplateTitle()
        viewBinding.root.setOnClickListener {
            onItemClick(module)
        }
    }

}