package io.github.caimucheng.leaf.ide.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentOpenSourceLicenseBinding
import io.github.caimucheng.leaf.ide.util.findGlobalNavController
import io.github.caimucheng.leaf.ide.util.getTextFromAssets
import io.github.caimucheng.leaf.ide.util.openWebPage
import org.json.JSONArray


class OpenSourceLicenseFragment : Fragment() {
    private lateinit var viewBinding: FragmentOpenSourceLicenseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentOpenSourceLicenseBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupToolbar()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        viewBinding.toolbar.setNavigationOnClickListener {
            findGlobalNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        val jsonText = requireContext().getTextFromAssets("open_source_license.json")
        val array = JSONArray(jsonText)
        val list = mutableListOf<ItemData>()
        val length = array.length()
        for (index in 0 until length) {
            val item = array.getJSONObject(index)
            list.add(
                ItemData(
                    name = item.getString("name"),
                    summary = item.getString("summary"),
                    license = item.getString("license"),
                    url = item.getString("url")
                )
            )
        }
        val recyclerView = viewBinding.recyclerView
        val adapter = RecyclerViewAdapter(requireContext(), list)
        val manager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = manager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = adapter
    }

    class RecyclerViewAdapter(
        private val context: Context,
        private val list: MutableList<ItemData>
    ) : RecyclerView.Adapter<RecyclerViewAdapter.ItemHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.fragment_open_source_license_list_item, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val item = list[position]
            holder.repoName.text = item.name
            holder.repoSummary.text = item.summary
            holder.repoLicense.text = item.license
            holder.root.setOnClickListener { openWebPage(context, item.url) }
        }

        class ItemHolder(item: View) : RecyclerView.ViewHolder(item) {
            val root: MaterialCardView = item.findViewById(R.id.root)
            val repoName: MaterialTextView = item.findViewById(R.id.repo_name)
            val repoSummary: MaterialTextView = item.findViewById(R.id.repo_summary)
            val repoLicense: MaterialTextView = item.findViewById(R.id.repo_license)
        }
    }

    data class ItemData(
        val name: String,
        val summary: String,
        val license: String,
        val url: String
    )
}
