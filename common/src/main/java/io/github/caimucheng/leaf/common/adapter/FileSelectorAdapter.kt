package io.github.caimucheng.leaf.common.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.caimucheng.leaf.common.R
import io.github.caimucheng.leaf.common.databinding.LayoutFileSelectorBinding
import java.io.File

class FileSelectorAdapter(
    private val context: Context,
    private val files: List<File>,
    private val matchingSuffix: List<String>?,
    private val onItemClick: (file: File) -> Unit
) : RecyclerView.Adapter<FileSelectorAdapter.ViewHolder>() {

    inner class ViewHolder(val viewBinding: LayoutFileSelectorBinding) :
        RecyclerView.ViewHolder(viewBinding.root)

    private val inflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutFileSelectorBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewBinding = holder.viewBinding
        val file = files[position]

        if (file.isDirectory) {
            viewBinding.icon.setBackgroundResource(R.drawable.baseline_folder_24)
        } else {
            if (matchingSuffix != null) {
                if (".${file.extension}" in matchingSuffix) {
                    viewBinding.icon.setBackgroundResource(R.drawable.baseline_plumbing_24)
                } else {
                    viewBinding.icon.setBackgroundResource(R.drawable.baseline_insert_drive_file_24)
                }
            } else {
                viewBinding.icon.setBackgroundResource(R.drawable.baseline_insert_drive_file_24)
            }
        }

        viewBinding.name.text = file.name
        viewBinding.root.setOnClickListener {
            onItemClick(file)
        }
    }

}