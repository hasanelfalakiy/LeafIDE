package io.github.caimucheng.leaf.ide.application

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.ide.broadcast.PluginBroadcastReceiver
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel

class AppContext : Application() {

    companion object {

        lateinit var current: AppContext
            private set

        lateinit var pluginBroadcastReceiver: BroadcastReceiver
            private set

    }

    override fun onCreate() {
        super.onCreate()
        current = this

        // Set default crash handler
        Thread.setDefaultUncaughtExceptionHandler(AppCrashHandler)

        Toasty.Config.getInstance()
            .allowQueue(false)
            .supportDarkTheme(true)
            .apply()

        pluginBroadcastReceiver = PluginBroadcastReceiver()

        // Register the broadcast
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED)
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
        intentFilter.addDataScheme("package")
        registerReceiver(pluginBroadcastReceiver, intentFilter)
    }

    override fun onTerminate() {
        AppViewModel.onCleared()
        unregisterReceiver(pluginBroadcastReceiver)
        super.onTerminate()
    }

}