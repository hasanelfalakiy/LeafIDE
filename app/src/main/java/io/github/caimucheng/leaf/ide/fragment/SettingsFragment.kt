package io.github.caimucheng.leaf.ide.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.FragmentSettingsBinding
import io.github.caimucheng.leaf.ide.util.findGlobalNavController

class SettingsFragment : Fragment() {

    private lateinit var viewBinding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentSettingsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupFragment()
    }

    private fun setupToolbar() {
        viewBinding.toolbar.setNavigationOnClickListener {
            findGlobalNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
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
            setPreferencesFromResource(R.xml.preference_settings, rootKey)
            setupPreference()
        }

        private fun setupPreference() {
            findPreference<Preference>("preference_about")?.setOnPreferenceClickListener {
                findGlobalNavController().navigate(R.id.action_settingsFragment_to_aboutFragment)
                true
            }

            findPreference<Preference>("preference_app_update")?.setOnPreferenceClickListener {
                findGlobalNavController().navigate(R.id.action_settingsFragment_to_appUpdateSettingsFragment)
                true
            }
        }
    }
}