plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "dev.xinto.argos"
    compileSdk = 35
    defaultConfig {
        applicationId = "dev.xinto.argos"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        manifestPlaceholders["appAuthRedirectScheme"] = "com.googleusercontent.apps.590553979193-1jilroobo7m2p55apfk1icuo0pktc9ru"
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugaring)

    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.appauth)

    implementation(libs.paging.compose)

    implementation(libs.androidx.browser)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.koin.android.compose)

    implementation(libs.navigationReimagined)

    implementation(libs.bundles.coil)

    implementation(libs.jsoup)
}