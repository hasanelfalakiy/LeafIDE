package leaf.plugin.nodejs.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.common.fragment.ProgressFragment
import io.github.caimucheng.leaf.plugin.fragment.PluginFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import leaf.plugin.nodejs.APP
import leaf.plugin.nodejs.R
import leaf.plugin.nodejs.databinding.FragmentNewProjectBinding
import leaf.plugin.nodejs.viewmodel.NewProjectCreateState
import leaf.plugin.nodejs.viewmodel.NewProjectIntent
import leaf.plugin.nodejs.viewmodel.NewProjectViewModel

class NewProjectFragment : PluginFragment() {

    private lateinit var viewBinding: FragmentNewProjectBinding

    private val newProjectViewModel: NewProjectViewModel by viewModels()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        val progressFragment = ProgressFragment()
        progressFragment.setTitle(APP.currentResources.getString(R.string.creating_project))
        progressFragment.setMessage(APP.currentResources.getString(R.string.creating_project_message))
        progressFragment.isCancelable = false

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                newProjectViewModel.state.collectLatest {
                    when (it.createState) {
                        NewProjectCreateState.Default -> {}
                        NewProjectCreateState.Loading -> {
                            if (!progressFragment.isAdded) {
                                progressFragment.show(childFragmentManager, "loading")
                            }
                        }

                        NewProjectCreateState.Success -> {
                            progressFragment.dismiss()
                            Toasty.success(
                                requireContext(),
                                APP.currentResources.getString(R.string.create_successfully)
                            )
                                .show()
                            actionHolder.popBackHome(refreshProject = true)
                        }

                        NewProjectCreateState.Failed -> {
                            progressFragment.dismiss()
                            val exception = it.exception
                            if (exception != null) {
                                Toasty.error(
                                    requireContext(),
                                    APP.currentResources.getString(
                                        R.string.creation_failed,
                                        exception.message
                                    )
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setup() {
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
        viewBinding.materialButton.setOnClickListener {
            val input = viewBinding.textInputEditText.text?.toString() ?: ""
            if (input.isEmpty()) {
                Toasty.info(
                    requireContext(),
                    APP.currentResources.getString(R.string.project_name_cannot_be_empty)
                ).show()
                return@setOnClickListener
            }

            if ('/' in input) {
                Toasty.info(
                    requireContext(),
                    APP.currentResources.getString(R.string.illegal_project_name)
                ).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                newProjectViewModel.intent.send(NewProjectIntent.Create(input))
            }
        }
    }

}