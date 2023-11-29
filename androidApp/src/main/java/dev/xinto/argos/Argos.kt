package dev.xinto.argos

import android.app.Application
import dev.xinto.argos.di.UiModule
import dev.xinto.argos.di.initArgosDi
import org.koin.android.ext.koin.androidContext

class Argos : Application() {

    override fun onCreate() {
        super.onCreate()

        initArgosDi {
            androidContext(this@Argos)

            modules(UiModule)
        }
    }

}