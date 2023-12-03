package leaf.plugin.nodejs.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import io.github.caimucheng.leaf.plugin.fragment.PluginFragment
import leaf.plugin.nodejs.APP
import leaf.plugin.nodejs.R

class NewProjectFragment : PluginFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val parser = APP.currentResources.getLayout(R.layout.fragment_new_project)
        return inflater.inflate(parser, container, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar: MaterialToolbar = view.findViewById(R.id.toolbar)
        toolbar.title = APP.currentResources.getString(R.string.template_title)
        toolbar.navigationIcon = APP.currentResources.getDrawable(
            R.drawable.baseline_arrow_back_24,
            requireContext().theme
        )
        toolbar.setNavigationOnClickListener {
            actionHolder.popBackStack()
        }

        val projectNameLayout: TextInputLayout = view.findViewById(R.id.projectNameLayout)
        projectNameLayout.startIconDrawable = APP.currentResources.getDrawable(
            R.drawable.baseline_data_object_24,
            requireContext().theme
        )
        projectNameLayout.hint = APP.currentResources.getString(R.string.project_name)

        val versionNameLayout: TextInputLayout = view.findViewById(R.id.versionLayout)
        versionNameLayout.startIconDrawable = APP.currentResources.getDrawable(
            R.drawable.baseline_swap_vert_24,
            requireContext().theme
        )
        versionNameLayout.hint = APP.currentResources.getString(R.string.version_name)
    }

}