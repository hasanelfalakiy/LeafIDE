package io.github.caimucheng.leaf.ide.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.LayoutMainModuleBinding
import io.github.caimucheng.leaf.ide.model.Module
import io.github.caimucheng.leaf.ide.model.description
import io.github.caimucheng.leaf.ide.model.icon
import io.github.caimucheng.leaf.ide.model.isEnabled
import io.github.caimucheng.leaf.ide.model.name
import io.github.caimucheng.leaf.ide.model.toggle

class MainModuleAdapter(
    private val context: Context,
    private val modules: List<Module>,
    private val onItemClick: (module: Module) -> Unit,
    private val onToggle: (module: Module) -> Unit
) : RecyclerView.Adapter<MainModuleAdapter.ViewHolder>() {

    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutMainModuleBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return modules.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val module = modules[position]
        val viewBinding = holder.viewBinding

        viewBinding.icon.background = module.icon
        viewBinding.title.text = module.name
        viewBinding.description.text = module.description
        viewBinding.description.post {
            val ellipsisCount =
                viewBinding.description.layout.getEllipsisCount(viewBinding.description.lineCount - 1)
            if (ellipsisCount > 0) {
                var isExpanded = false
                viewBinding.expand.visibility = View.VISIBLE
                viewBinding.expand.setOnClickListener {
                    isExpanded = !isExpanded
                    if (isExpanded) {
                        viewBinding.expand.text = context.getString(R.string.collapse)
                        viewBinding.description.maxLines = Int.MAX_VALUE
                    } else {
                        viewBinding.expand.text = context.getString(R.string.expand)
                        viewBinding.description.maxLines = 2
                    }
                }
            }
        }
        viewBinding.root.setOnClickListener {
            onItemClick(module)
        }
        viewBinding.root.setOnCreateContextMenuListener { menu, _, _ ->
            val menuInflater = MenuInflater(context)
            menu.setHeaderTitle(module.name)
            menuInflater.inflate(R.menu.menu_main_module_popup, menu)

            val titleResId = if (module.isEnabled) R.string.disable else R.string.enable
            val enableItem = menu.findItem(R.id.enable)
            enableItem.title = context.getString(titleResId)
            enableItem.setOnMenuItemClickListener {
                module.toggle()
                enableItem.title =
                    context.getString(if (module.isEnabled) R.string.disable else R.string.enable)

                onToggle(module)
                false
            }
        }

        if (module.isEnabled) {
            viewBinding.constraintLayout.alpha = 1f
        } else {
            viewBinding.constraintLayout.alpha = 0.6f
        }
    }

    inner class ViewHolder(val viewBinding: LayoutMainModuleBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

}