plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialize)
    alias(libs.plugins.ktlint.jlleitschuh)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services") // Agregar esto
    id("com.google.firebase.crashlytics")
    id("kotlin-android")
    id("dagger.hilt.android.plugin")

}

android {
    namespace = "com.mundocode.moneyflow"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mundocode.moneyflow"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("D:/Proyectos/MoneyFlow2.jks") // Ruta del keystore
            storePassword = "98092410"
            keyAlias = "MoneyFlow2"
            keyPassword = "98092410"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.foundation)

    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.navigation.compose)

    // LiveData
    implementation(libs.androidx.runtime.livedata)

    // Icons
    implementation(libs.androidx.material.icons.extended)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.datastore.preferences)

    // Biblioteca para manipular archivos PDF
    implementation(libs.itext7.core)

    // Biblioteca para manipular archivos Excel
    implementation(libs.poi.ooxml)

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0") // Librería de gráficos


    // Firebase BoM (Gestor de versiones de Firebase)
    implementation(platform(libs.firebase.bom))

    // Firebase Authentication
    implementation(libs.firebase.auth.ktx)

    // Firestore (Base de datos en la nube)
    implementation(libs.firebase.firestore.ktx)

    // Google Sign-In (Si vas a permitir autenticación con Google)
    implementation(libs.play.services.auth)

    // Firebase Storage (Opcional, si necesitas subir imágenes o archivos)
    implementation(libs.firebase.storage.ktx)

    // DataStore para guardar configuraciones locales (Opcional, útil para guardar el rol localmente)
    implementation(libs.androidx.datastore.preferences)

    // ML Kit - OCR
    implementation(libs.text.recognition)

    // CameraX para capturar imágenes
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Activity Result API
    implementation(libs.androidx.activity.ktx)

    //coil
    implementation(libs.coil.compose)

    // Dependencia de ML Kit para escaneo de códigos de barras
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

    //Gson
    implementation(libs.gson)

    implementation(libs.androidx.biometric)

    implementation("org.slf4j:slf4j-api:2.0.4")
    implementation("org.slf4j:slf4j-simple:1.7.30") // O usa otra implementación como logback


    // Timber
    implementation(libs.timber)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.bundles.firebaseBundle)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Acompanist
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pager.indicators)

}

ktlint {
    version.set("1.5.0")
    debug.set(true)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    enableExperimentalRules.set(true)
//    baseline.set(file("ktlint-baseline.xml"))
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

kotlin {
    sourceSets.configureEach {
        languageSettings.enableLanguageFeature("ExplicitBackingFields")
    }
}
