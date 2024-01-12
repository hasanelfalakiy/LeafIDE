package io.github.caimucheng.leaf.ide.application

import android.app.Application
import es.dmoral.toasty.Toasty
import io.github.caimucheng.leaf.ide.viewmodel.AppViewModel

class AppContext : Application() {

    companion object {

        lateinit var current: AppContext
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
    }

    override fun onTerminate() {
        AppViewModel.onCleared()
        super.onTerminate()
    }

}