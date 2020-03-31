package com.example.mapchat

import android.app.Application
import com.example.mapchat.di.AppComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BaseApplication)

            modules(AppComponent)
        }
    }


}