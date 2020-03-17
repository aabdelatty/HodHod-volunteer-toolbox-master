package com.hodhod.hodohodadmin

import android.app.Application
import com.hodhod.hodohodadmin.service.remoteModule
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/Neo_Sans_Arabic_Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build())

        startKoin {
            androidContext(this@App)
            androidLogger()
            modules(listOf(remoteModule))
        }

    }
}