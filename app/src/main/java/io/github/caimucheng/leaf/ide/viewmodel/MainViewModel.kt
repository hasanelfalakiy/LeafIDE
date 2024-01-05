package io.github.caimucheng.leaf.ide.viewmodel

import androidx.lifecycle.ViewModel
import io.github.caimucheng.leaf.ide.depository.MainDepository

class MainViewModel : ViewModel() {

    private val mainDepository: MainDepository = MainDepository()

    fun initialize() {
        mainDepository.initialize()
    }

}