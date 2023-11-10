package io.github.caimucheng.leaf.ide.application

import android.app.Application

class AppContext : Application() {

    companion object {

        lateinit var current: AppContext
            private set

    }

    override fun onCreate() {
        super.onCreate()
        current = this
    }

}