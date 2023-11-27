package leaf.plugin.nodejs.fragment

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.caimucheng.leaf.plugin.fragment.PluginFragment
import leaf.plugin.nodejs.APP
import leaf.plugin.nodejs.R
import leaf.plugin.nodejs.databinding.FragmentNewProjectBinding

class NewProjectFragment : PluginFragment() {

    private lateinit var viewBinding: FragmentNewProjectBinding

    private inline val mResources: Resources
        get() {
            return APP.currentResources
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentNewProjectBinding.bind(
            inflater.inflate(
                mResources.getLayout(R.layout.fragment_new_project),
                container,
                false
            )
        )
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupContent()
    }

    private fun setupContent() {
        viewBinding.materialToolbar.title = mResources.getString(R.string.template_title)
        viewBinding.materialToolbar.setNavigationOnClickListener {
            actionHolder.popBackStack()
        }

        viewBinding.create.text = mResources.getString(R.string.create)
        viewBinding.create.setOnClickListener {

        }
    }

}