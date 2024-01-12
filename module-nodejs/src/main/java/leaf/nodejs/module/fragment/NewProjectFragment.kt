package leaf.nodejs.module.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.github.caimucheng.leaf.module.fragment.ModuleFragment
import leaf.nodejs.module.R
import leaf.nodejs.module.databinding.FragmentNewNodejsProjectBinding

class NewProjectFragment : ModuleFragment() {

    private lateinit var viewBinding: FragmentNewNodejsProjectBinding

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
    }

}