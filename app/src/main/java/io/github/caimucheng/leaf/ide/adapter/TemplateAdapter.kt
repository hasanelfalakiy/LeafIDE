package io.github.caimucheng.leaf.ide.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.ide.databinding.LayoutTemplateBinding

class TemplateAdapter(
    private val context: Context
) : RecyclerView.Adapter<TemplateAdapter.ViewHolder>() {

    inner class ViewHolder(val viewBinding: LayoutTemplateBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private val inflater by lazy { LayoutInflater.from(context) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutTemplateBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

}