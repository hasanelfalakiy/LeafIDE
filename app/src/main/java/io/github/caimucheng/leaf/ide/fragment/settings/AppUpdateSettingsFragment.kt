package io.github.caimucheng.leaf.ide.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentSettingsAppUpdateBinding
import io.github.caimucheng.leaf.ide.util.findGlobalNavController
import io.github.caimucheng.leaf.ide.util.getVersionNameSelf

class AppUpdateSettingsFragment : Fragment() {
    private lateinit var viewBinding: FragmentSettingsAppUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSettingsAppUpdateBinding.inflate(inflater, container, false)
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
            setPreferencesFromResource(R.xml.preference_settings_app_update, rootKey)
            setupPreference()
        }

        private fun setupPreference() {
            findPreference<Preference>("app_version_name")
                ?.summary = getVersionNameSelf(requireContext())
        }
    }
}