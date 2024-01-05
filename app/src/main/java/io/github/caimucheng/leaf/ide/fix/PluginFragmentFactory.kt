package io.github.caimucheng.leaf.ide.fix

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel

class PluginFragmentFactory : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        try {
            return super.instantiate(classLoader, className)
        } catch (e: Exception) {
            val plugins = AppViewModel.state.value.plugins
            for (plugin in plugins) {
                try {
                    return super.instantiate(plugin.pluginClassLoader, className)
                } catch (_: Exception) {}
            }
            throw e
        }
    }

}