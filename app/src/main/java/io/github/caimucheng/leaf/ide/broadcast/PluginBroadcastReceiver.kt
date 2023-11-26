package io.github.caimucheng.leaf.ide.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.caimucheng.leaf.ide.viewmodel.AppIntent
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel

class PluginBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val extraReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false)
        when {
            !extraReplacing && intent.action == Intent.ACTION_PACKAGE_ADDED -> {
                Log.e("Broadcast", "App added")
            }

            !extraReplacing && intent.action == Intent.ACTION_PACKAGE_REMOVED -> {
                Log.e("Broadcast", "App removed")
            }

            intent.action == Intent.ACTION_PACKAGE_REPLACED -> {
                Log.e("Broadcast", "App replaced")
            }
        }
    }

}