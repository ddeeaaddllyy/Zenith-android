package com.ddeeaaddllyy.zenith

import android.app.Application
import com.ddeeaaddllyy.zenith.di.AppContainer

class ZenithApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
