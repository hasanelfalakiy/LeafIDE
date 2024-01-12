package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import io.github.caimucheng.leaf.common.util.ViewUtils
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentProjectEditorBinding
import io.github.caimucheng.leaf.ide.util.findGlobalNavController
import io.github.caimucheng.leaf.ide.viewmodel.ProjectEditorIntent
import io.github.caimucheng.leaf.ide.viewmodel.ProjectEditorViewModel
import io.github.caimucheng.leaf.ide.viewmodel.ProjectStatus
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProjectEditorFragment : Fragment() {
    private lateinit var viewBinding: FragmentProjectEditorBinding

    private var undoMenuItem: MenuItem? = null
    private var redoMenuItem: MenuItem? = null
    private var saveMenuItem: MenuItem? = null
    private var searchInsideFileMenuItem: MenuItem? = null

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (viewBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                viewBinding.drawerLayout.closeDrawers()
            } else {
                viewLifecycleOwner.lifecycleScope.launch {
                    ProjectEditorViewModel.intent.send(ProjectEditorIntent.CloseProject)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentProjectEditorBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val projectPath = requireArguments().getString("projectPath")
        if (projectPath.isNullOrBlank()) {
            findGlobalNavController().popBackStack()
        }

        setupToolbar()
        setupEditor()
        setupFooter()

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )

        viewLifecycleOwner.lifecycleScope.launch {
            ProjectEditorViewModel.intent.send(ProjectEditorIntent.OpenProject(projectPath))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            ProjectEditorViewModel.state.collectLatest {
                when (it.projectStatus) {
                    ProjectStatus.CLEAR -> {

                    }

                    ProjectStatus.FREE -> {
                        ViewUtils.disableMenuItem(
                            undoMenuItem,
                            redoMenuItem,
                            saveMenuItem,
                            searchInsideFileMenuItem
                        )
                        ViewUtils.goneView(
                            viewBinding.loading,
                            viewBinding.placeholder,
                            viewBinding.editor
                        )
                    }

                    ProjectStatus.LOADING -> {
                        ViewUtils.disableMenuItem(
                            undoMenuItem,
                            redoMenuItem,
                            saveMenuItem,
                            searchInsideFileMenuItem
                        )
                        ViewUtils.visibilityView(viewBinding.loading)
                        ViewUtils.goneView(
                            viewBinding.placeholder,
                            viewBinding.editor
                        )
                    }

                    ProjectStatus.ERROR -> {

                    }

                    ProjectStatus.DONE -> {
                        ViewUtils.visibilityView(viewBinding.placeholder)
                        ViewUtils.goneView(
                            viewBinding.loading,
                            viewBinding.editor
                        )
                        if (it.editorCurrentContent != null) {
                            ViewUtils.enableMenuItem(
                                undoMenuItem,
                                redoMenuItem,
                                saveMenuItem,
                                searchInsideFileMenuItem
                            )
                        } else {
                            ViewUtils.disableMenuItem(
                                undoMenuItem,
                                redoMenuItem,
                                saveMenuItem,
                                searchInsideFileMenuItem
                            )
                        }
                    }

                    ProjectStatus.CLOSE -> {
                        ProjectEditorViewModel.intent.send(ProjectEditorIntent.Clear)
                        findGlobalNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = viewBinding.toolbar
        val drawerLayout = viewBinding.drawerLayout
        val drawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            toolbar,
            R.string.drawerlayout_expand,
            R.string.drawerlayout_shrink
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        undoMenuItem = toolbar.menu.findItem(R.id.undo)
        redoMenuItem = toolbar.menu.findItem(R.id.redo)
        saveMenuItem = toolbar.menu.findItem(R.id.save)
        searchInsideFileMenuItem = toolbar.menu.findItem(R.id.search_inside_file)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.close_project -> {
                    if (view != null) {
                        viewLifecycleOwner.lifecycleScope.launch {
                            ProjectEditorViewModel.intent.send(ProjectEditorIntent.CloseProject)
                        }
                    }
                    return@setOnMenuItemClickListener true
                }

                else -> return@setOnMenuItemClickListener false
            }
        }
    }

    private fun setupEditor() {

    }

    private fun setupFooter() {
        val symbolInputView = viewBinding.symbolInputView
        symbolInputView.addSymbols(
            arrayOf(
                "Tab", "{", "}", "(", ")", ",", ".", ";", "\"", "?",
                "+", "-", "*", "/", "<", ">", "[", "]", ":"
            ),
            arrayOf(
                "\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?",
                "+", "-", "*", "/", "<", ">", "[", "]", ":"
            )
        )
        symbolInputView.bindEditor(viewBinding.editor)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.remove()
    }
}