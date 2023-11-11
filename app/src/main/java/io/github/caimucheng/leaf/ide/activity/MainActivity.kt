package io.github.caimucheng.leaf.ide.activity

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import io.github.caimucheng.leaf.ide.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}