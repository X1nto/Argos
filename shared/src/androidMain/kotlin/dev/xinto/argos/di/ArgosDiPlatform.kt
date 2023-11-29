package dev.xinto.argos.di

import dev.xinto.argos.local.account.ArgosAccountManager
import dev.xinto.argos.local.settings.ArgosSettings
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal actual object ArgosDiPlatform {

    actual val LocalModule: Module = module {
        singleOf(::ArgosAccountManager)
        singleOf(::ArgosSettings)
    }

}
