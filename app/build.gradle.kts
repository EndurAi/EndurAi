
plugins {
    jacoco
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.sonar)
    alias(libs.plugins.gms)
}

android {
    namespace = "com.android.sample"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.sample"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            // You need to specify either an absolute path or include the
            // keystore file in the same directory as the build.gradle file.
            storeFile = file("EndurAi.jks")
            storePassword = "EndurAi"
            keyAlias = "EndurAiKeyStore"
            keyPassword = "EndurAi"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    testCoverage {
        jacocoVersion = "0.8.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    // Robolectric needs to be run only in debug. But its tests are placed in the shared source set (test)
    // The next lines transfers the src/test/* from shared to the testDebug one
    //
    // This prevent errors from occurring during unit tests
    sourceSets.getByName("testDebug") {
        val test = sourceSets.getByName("test")

        java.setSrcDirs(test.java.srcDirs)
        res.setSrcDirs(test.res.srcDirs)
        resources.setSrcDirs(test.resources.srcDirs)
    }

    sourceSets.getByName("test") {
        java.setSrcDirs(emptyList<File>())
        res.setSrcDirs(emptyList<File>())
        resources.setSrcDirs(emptyList<File>())
    }
}

sonar {
    properties {
        property("sonar.projectKey", "EndurAi_EndurAi")
        property("sonar.projectName", "EndurAi")
        property("sonar.organization", "enduraimakesmybodymoves1919")
        property("sonar.host.url", "https://sonarcloud.io")
        // Comma-separated paths to the various directories containing the *.xml JUnit report files. Each path may be absolute or relative to the project base directory.
        property("sonar.junit.reportPaths", "${project.layout.buildDirectory.get()}/test-results/testDebugunitTest/")
        // Paths to xml files with Android Lint issues. If the main flavor is changed, this file will have to be changed too.
        property("sonar.androidLint.reportPaths", "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml")
        // Paths to JaCoCo XML coverage report files.
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
}

// When a library is used both by robolectric and connected tests, use this function
fun DependencyHandlerScope.globalTestImplementation(dep: Any) {
    androidTestImplementation(dep)
    testImplementation(dep)
}

dependencies {
    // Firebase
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation(libs.firebase.storage.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.espresso.intents)
    implementation(libs.androidx.ui.test.android)
  implementation(libs.androidx.camera.video)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    globalTestImplementation(libs.androidx.junit)
    globalTestImplementation(libs.androidx.espresso.core)

    // Activity Compose for rememberLauncherForActivityResult
    implementation(libs.coil.compose)

    // DataStore local caching
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.gson)

    // ------------- Jetpack Compose ------------------
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    globalTestImplementation(composeBom)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    // Material Design 3
    implementation(libs.compose.material3)
    // Integration with activities
    implementation(libs.compose.activity)
    // Integration with ViewModels
    implementation(libs.compose.viewmodel)
    // Android Studio Preview support
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    // UI Tests
    globalTestImplementation(libs.compose.test.junit)
    debugImplementation(libs.compose.test.manifest)

    // --------- Kaspresso test framework ----------
    globalTestImplementation(libs.kaspresso)
    globalTestImplementation(libs.kaspresso.compose)

    // ----------       Robolectric     ------------
    testImplementation(libs.robolectric)

    // --------- Moshi dependencies ----------
    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    implementation (libs.moshi.sealed.runtime)
    implementation (libs.moshi.sealed.reflect)
    implementation (libs.moshi.adapters)

    // --------- Coil dependencies ----------
    implementation(libs.coil.compose)
//    implementation(libs.coil.core)

    val camerax_version = "1.3.0-alpha07"
    //// CAMERA STUFF ////
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")
    implementation ("androidx.camera:camera-view:$camerax_version")
    implementation ("androidx.camera:camera-extensions:$camerax_version")

    //// ACCOMPANIST ////
implementation("com.google.accompanist:accompanist-permissions:0.30.1")

    //----------- Test dependencies ---------------
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockk)
//    testImplementation "androidx.arch.core:core-testing:2.1.0"
    
    testImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.mockito.android)
    androidTestImplementation(libs.mockito.kotlin)

    configurations.configureEach {
        exclude(group = "com.google.protobuf", module = "protobuf-lite")
    }

    implementation(libs.kotlinx.datetime)

    //Google ML kit
    implementation("com.google.mlkit:pose-detection:18.0.0-beta5")
}

tasks.withType<Test> {
    // Configure Jacoco for each tests
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required = true
        html.required = true
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
    )

    val debugTree = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.layout.projectDirectory}/src/main/java"
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
    })
}
