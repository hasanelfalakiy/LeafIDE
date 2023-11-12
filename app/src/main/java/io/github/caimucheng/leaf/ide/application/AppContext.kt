package io.github.caimucheng.leaf.ide.application

import android.app.Application
import es.dmoral.toasty.Toasty

class AppContext : Application() {

    companion object {

        lateinit var current: AppContext
            private set

    }

    override fun onCreate() {
        super.onCreate()
        current = this

        Toasty.Config.getInstance()
            .allowQueue(false)
            .supportDarkTheme(true)
            .apply()
    }

}