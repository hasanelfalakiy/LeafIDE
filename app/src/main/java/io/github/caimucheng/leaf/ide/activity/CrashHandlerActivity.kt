package io.github.caimucheng.leaf.ide.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.github.caimucheng.leaf.ide.R
import io.github.caimucheng.leaf.ide.databinding.ActivityCrashHandlerBinding

class CrashHandlerActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityCrashHandlerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCrashHandlerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val deviceInfo = intent.getStringExtra("deviceInfo") ?: "null"
        val threadGroup = intent.getStringExtra("threadGroup") ?: "null"
        val thread = intent.getStringExtra("thread") ?: "null"
        val exception = intent.getStringExtra("exception") ?: "null"
        val all = buildString {
            appendLine(getString(R.string.app_crashed_header))
            appendLine()
            appendLine(getString(R.string.device_info))
            appendLine(deviceInfo)
            appendLine()
            appendLine(getString(R.string.thread_group, threadGroup))
            appendLine(getString(R.string.thread, thread))
            appendLine(getString(R.string.exception, exception))
        }
        viewBinding.description.text = all
        viewBinding.copyButton.setOnClickListener {
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText("exception", all))
        }
        viewBinding.restartButton.setOnClickListener {
            val intent =
                packageManager.getLaunchIntentForPackage(packageName) ?: return@setOnClickListener
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

}