package com.mundocode.moneyflow

import android.app.Application
import com.mundocode.moneyflow.core.CrashReportingTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MoneyFlowApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(
            if (BuildConfig.DEBUG) {
                Timber.DebugTree()
            } else {
                CrashReportingTree()
            },
        )
    }
}

