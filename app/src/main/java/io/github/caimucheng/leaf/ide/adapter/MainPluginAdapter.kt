package io.github.caimucheng.leaf.ide.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.application.AppContext
import io.github.caimucheng.leaf.ide.databinding.LayoutMainPluginBinding
import io.github.caimucheng.leaf.ide.model.Plugin
import io.github.caimucheng.leaf.ide.model.description
import io.github.caimucheng.leaf.ide.model.name

class MainPluginAdapter(
    private val context: Context,
    private val plugins: List<Plugin>
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
                        viewBinding.constraintLayout.visibility = View.INVISIBLE
                        viewBinding.expand.text = context.getString(R.string.collapse)
                        viewBinding.description.maxLines = Int.MAX_VALUE
                        viewBinding.constraintLayout.visibility = View.VISIBLE
                    } else {
                        viewBinding.constraintLayout.visibility = View.INVISIBLE
                        viewBinding.expand.text = context.getString(R.string.expand)
                        viewBinding.description.maxLines = 2
                        viewBinding.constraintLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    inner class ViewHolder(val viewBinding: LayoutMainPluginBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

}