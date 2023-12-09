package leaf.plugin.nodejs.fragment

import android.annotation.SuppressLint
import android.content.ContextWrapper
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import io.github.caimucheng.leaf.plugin.fragment.PluginFragment
import leaf.plugin.nodejs.APP
import leaf.plugin.nodejs.R
import leaf.plugin.nodejs.databinding.FragmentNewProjectBinding

class NewProjectFragment : PluginFragment() {

    private lateinit var viewBinding: FragmentNewProjectBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentNewProjectBinding.bind(
            inflater.inflate(
                APP.currentResources.getLayout(R.layout.fragment_new_project),
                container,
                false
            )
        )
        return viewBinding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.toolbar.title = APP.currentResources.getString(R.string.template_title)
        viewBinding.toolbar.navigationIcon = APP.currentResources.getDrawable(
            R.drawable.baseline_arrow_back_24,
            requireContext().theme
        )
        viewBinding.toolbar.setNavigationIconTint(
            ContextCompat.getColor(
                requireContext(),
                io.github.caimucheng.leaf.common.R.color.colorOnSurface
            )
        )
        viewBinding.toolbar.setNavigationOnClickListener {
            actionHolder.popBackStack()
        }
        viewBinding.textInputLayout.startIconDrawable = APP.currentResources.getDrawable(
            R.drawable.baseline_data_object_24,
            requireContext().theme
        )
        viewBinding.textInputLayout.hint = APP.currentResources.getString(R.string.project_name)

        viewBinding.materialButton.text = APP.currentResources.getString(R.string.create)
    }

}