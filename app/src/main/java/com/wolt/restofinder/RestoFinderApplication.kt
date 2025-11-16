package com.wolt.restofinder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class RestoFinderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeTimber()
    }

    /**
     * Initialize Timber logging with appropriate tree for build variant.
     */
    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("Timber initialized for DEBUG build")
        }
    }
}
