plugins {
    id("com.android.application")
}

// Signing values come from env vars (CI) or ~/.gradle / local gradle.properties.
fun secret(name: String): String? =
    System.getenv(name) ?: (project.findProperty(name) as String?)

val keystorePath = secret("TAPTALLY_KEYSTORE") ?: "upload.jks"
val keystoreFile = file(keystorePath)
val keystorePassword = secret("TAPTALLY_KEYSTORE_PASSWORD")

android {
    namespace = "com.vitalikhakim.taptally"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vitalikhakim.taptally"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        create("release") {
            if (keystoreFile.exists() && keystorePassword != null) {
                storeFile = keystoreFile
                storePassword = keystorePassword
                keyAlias = "upload"
                keyPassword = keystorePassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            if (keystoreFile.exists() && keystorePassword != null) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
