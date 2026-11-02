package com.aaryan.pulse

import android.app.Application
import com.aaryan.pulse.common.injections.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PulseApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PulseApplication)
            modules(appModules)
        }
    }
}