package io.github.caimucheng.leaf.ide.activity

import android.os.Bundle
import io.github.caimucheng.leaf.ide.databinding.ActivityMainBinding
import java.lang.ref.WeakReference

class MainActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    companion object {
        var currentMainActivity: WeakReference<MainActivity>? = null
            private set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentMainActivity = WeakReference(this)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
    }
}