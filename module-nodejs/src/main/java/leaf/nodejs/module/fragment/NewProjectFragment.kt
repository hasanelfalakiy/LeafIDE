package leaf.nodejs.module.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.common.fragment.ProgressFragment
import io.github.caimucheng.leaf.module.fragment.ModuleFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import leaf.nodejs.module.NodeJSModuleAPP
import leaf.nodejs.module.R
import leaf.nodejs.module.databinding.FragmentNewNodejsProjectBinding
import leaf.nodejs.module.viewmodel.NewProjectCreateState
import leaf.nodejs.module.viewmodel.NewProjectIntent
import leaf.nodejs.module.viewmodel.NewProjectViewModel
import java.io.File

class NewProjectFragment : ModuleFragment() {

    private lateinit var viewBinding: FragmentNewNodejsProjectBinding
    private val newProjectViewModel: NewProjectViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentNewNodejsProjectBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        val progressFragment = ProgressFragment()
        progressFragment.setTitle(requireContext().getString(R.string.creating_project))
        progressFragment.setMessage(requireContext().getString(R.string.creating_project_message))
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
                                getString(R.string.create_successfully)
                            ).show()
                            actionHolder.popBackHome(refreshProject = true)
                        }

                        NewProjectCreateState.Failed -> {
                            progressFragment.dismiss()
                            val exception = it.exception
                            if (exception != null) {
                                Toasty.error(
                                    requireContext(),
                                    getString(R.string.creation_failed, exception.message)
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setup() {
        viewBinding.toolbar.setNavigationOnClickListener {
            actionHolder.popBackStack()
        }
        viewBinding.materialButton.setOnClickListener {
            val projectNameInput = viewBinding.projectNameEditText.text?.toString() ?: ""
            val descriptionInput = viewBinding.descriptionEditText.text?.toString() ?: ""
            if (projectNameInput.isEmpty()) {
                Toasty.info(
                    requireContext(),
                    getString(R.string.project_name_cannot_be_empty)
                ).show()
                return@setOnClickListener
            }

            if (descriptionInput.isEmpty()) {
                Toasty.info(
                    requireContext(),
                    getString(R.string.description_cannot_be_null)
                ).show()
                return@setOnClickListener
            }

            if ('/' in projectNameInput) {
                Toasty.info(
                    requireContext(),
                    getString(R.string.illegal_project_name)
                ).show()
                return@setOnClickListener
            }

            if (File(NodeJSModuleAPP.currentPaths.leafIDEProjectPath, projectNameInput).exists()) {
                Toasty.info(
                    requireContext(),
                    getString(R.string.exists_project)
                ).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                newProjectViewModel.intent.send(
                    NewProjectIntent.Create(
                        projectNameInput,
                        descriptionInput
                    )
                )
            }
        }
    }
}