package io.github.caimucheng.leaf.ide.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentSettingsEditorBinding
import io.github.caimucheng.leaf.ide.util.findGlobalNavController

class EditorSettingsFragment : Fragment() {
    private lateinit var viewBinding: FragmentSettingsEditorBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSettingsEditorBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupFragment()
    }

    private fun setupToolbar() {
        viewBinding.toolbar.setNavigationOnClickListener {
            findGlobalNavController().popBackStack()
        }
    }

    private fun setupFragment() {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_settings, SettingsFragmentContainer())
            .commitAllowingStateLoss()
    }

    class SettingsFragmentContainer : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preference_settings_editor, rootKey)
            setupPreference()
        }

        private fun setupPreference() {

        }
    }
}