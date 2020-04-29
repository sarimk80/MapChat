package com.example.mapchat

import android.app.Application
import android.util.Log
import androidx.core.provider.FontRequest
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.FontRequestEmojiCompatConfig
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


        val fontRequest = FontRequest(
            "com.google.android.gms.fonts",
            "com.google.android.gms",
            "Noto Color Emoji Compat",
            R.array.com_google_android_gms_fonts_certs
        )
        val config =
            FontRequestEmojiCompatConfig(applicationContext, fontRequest).setReplaceAll(true)
                .registerInitCallback(object : EmojiCompat.InitCallback() {
                    override fun onInitialized() {
                        super.onInitialized()
                        Log.d("Emoji", "Emoji initialized")
                    }

                    override fun onFailed(throwable: Throwable?) {
                        super.onFailed(throwable)
                        Log.d("Emoji", "${throwable?.toString()}")
                    }
                })


        EmojiCompat.init(config)
    }


}