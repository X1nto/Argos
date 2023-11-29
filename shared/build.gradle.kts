plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    applyDefaultHierarchyTemplate()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.ktor)
            implementation(libs.koin.core)
            implementation(libs.paging.common)
            api(libs.kotlinx.datetime)
        }
        androidMain.dependencies {
            implementation(libs.androidx.crypto)
            implementation(libs.androidx.core)
            implementation(libs.ktor.android)
            implementation(libs.koin.android)
        }
        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "dev.xinto.argos"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
}