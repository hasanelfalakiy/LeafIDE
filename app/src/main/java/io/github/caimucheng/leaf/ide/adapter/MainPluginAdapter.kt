package io.github.caimucheng.leaf.ide.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.LayoutMainPluginBinding
import io.github.caimucheng.leaf.ide.model.Plugin
import io.github.caimucheng.leaf.ide.model.description
import io.github.caimucheng.leaf.ide.model.isEnabled
import io.github.caimucheng.leaf.ide.model.isSupported
import io.github.caimucheng.leaf.ide.model.name
import io.github.caimucheng.leaf.ide.model.toggle
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel

class MainPluginAdapter(
    private val context: Context,
    private val plugins: List<Plugin>,
    private val onToggle: (plugin: Plugin) -> Unit,
    private val onUninstall: (plugin: Plugin) -> Unit
) : RecyclerView.Adapter<MainPluginAdapter.ViewHolder>() {

    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutMainPluginBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return plugins.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val plugin = plugins[position]
        val viewBinding = holder.viewBinding

        viewBinding.icon.background = plugin.icon
        viewBinding.title.text = plugin.name
        viewBinding.description.text = plugin.description
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
        viewBinding.root.setOnCreateContextMenuListener { menu, _, _ ->
            val menuInflater = MenuInflater(context)
            menu.setHeaderTitle(plugin.name)
            menuInflater.inflate(R.menu.menu_main_plugin_popup, menu)

            val titleResId = if (plugin.isEnabled) R.string.disable else R.string.enable
            val enableItem = menu.findItem(R.id.enable)
            enableItem.title = context.getString(titleResId)
            enableItem.setOnMenuItemClickListener {
                plugin.toggle()
                enableItem.title =
                    context.getString(if (plugin.isEnabled) R.string.disable else R.string.enable)

                onToggle(plugin)
                false
            }

            val uninstallItem = menu.findItem(R.id.uninstall)
            uninstallItem.setOnMenuItemClickListener {
                onUninstall(plugin)
                false
            }
        }

        if (plugin.isEnabled && plugin.isSupported) {
            viewBinding.constraintLayout.alpha = 1f
        } else {
            viewBinding.constraintLayout.alpha = 0.6f
        }

        if (!plugin.isSupported) {
            viewBinding.unsupported.visibility = View.VISIBLE
        }
    }

    inner class ViewHolder(val viewBinding: LayoutMainPluginBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

}