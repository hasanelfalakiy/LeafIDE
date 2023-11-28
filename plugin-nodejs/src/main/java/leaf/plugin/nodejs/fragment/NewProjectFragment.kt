package leaf.plugin.nodejs.fragment

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.dmoral.toasty.Toasty
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setupContent() {
        viewBinding.materialToolbar.title = mResources.getString(R.string.template_title)
        viewBinding.materialToolbar.setNavigationOnClickListener {
            actionHolder.popBackStack()
        }

        viewBinding.projectName.hint = mResources.getString(R.string.project_name)
        viewBinding.projectName.startIconDrawable = mResources.getDrawable(
            R.drawable.baseline_data_object_24,
            requireContext().theme
        )

        viewBinding.version.hint = mResources.getString(R.string.version)
        viewBinding.version.startIconDrawable = mResources.getDrawable(
            R.drawable.baseline_swap_vert_24,
            requireContext().theme
        )

        viewBinding.create.text = mResources.getString(R.string.create)
        viewBinding.create.setOnClickListener {
            val projectNameText = viewBinding.projectNameInput.text ?: return@setOnClickListener
            val versionText = viewBinding.versionInput.text ?: return@setOnClickListener

            if (projectNameText.isEmpty()) {
                Toasty.info(
                    requireContext(),
                    mResources.getString(R.string.project_name_cannot_be_empty)
                )
                    .show()
                return@setOnClickListener
            }

            if (versionText.isEmpty()) {
                Toasty.info(
                    requireContext(),
                    mResources.getString(R.string.version_cannot_be_empty)
                )
                    .show()
                return@setOnClickListener
            }

            if ('/' in projectNameText) {
                Toasty.info(requireContext(), mResources.getString(R.string.invalid_project_name))
                    .show()
                return@setOnClickListener
            }

            if (!"^\\d+(?:\\.\\d+)*\$".toRegex().matches(versionText)) {
                Toasty.info(requireContext(), mResources.getString(R.string.invalid_version))
                    .show()
                return@setOnClickListener
            }
        }
    }

}