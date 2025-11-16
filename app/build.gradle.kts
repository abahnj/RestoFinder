plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    jacoco
}

android {
    namespace = "com.wolt.restofinder"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.wolt.restofinder"
        minSdk = 33
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        // ASSUMPTION: Using debug signing for assignment purposes
        // Production: Would use separate release keystore with secured credentials
        getByName("debug") {
            storeFile = file("${System.getProperty("user.home")}/.android/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Use debug signing for assignment purposes
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
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

    buildToolsVersion = "36.1.0"
}

dependencies {
    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Dependency Injection - Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp.logging.interceptor)

    // Persistence
    implementation(libs.androidx.datastore.preferences)

    // Async - Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Image Loading
    implementation(libs.coil.compose)
    implementation(libs.compose.shimmer)

    // Logging
    implementation(libs.timber)

    // Testing - Unit
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)

    // Testing - Integration
    testImplementation(libs.mockwebserver)

    // Testing - Android
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.turbine)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    kspAndroidTest(libs.hilt.android.compiler)

    // Debug
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}

// JaCoCo Configuration
jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

// File exclusion patterns for generated code
val coverageExclusions = listOf(
    // Android generated
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/*Test*.*",
    "android/**/*.*",

    // Hilt generated
    "**/*_HiltModules*",
    "**/*_Factory*",
    "**/*_MembersInjector*",
    "**/Hilt_*",
    "**/*_Impl*",
    "**/*Module_*",
    "**/*_AssistedFactory*",
    "**/*_ComponentTreeDeps*",

    // Compose generated
    "**/*$$*.class",
    "**/*ComposableSingletons*",

    // Data Transfer Objects (no business logic)
    "**/dto/**",

    // Application class (just Hilt setup)
    "**/RestoFinderApplication*",

    // UI Components (tested via instrumented tests, not unit tests)
    "**/presentation/common/AnimatedLocationDisplayKt*",
    "**/presentation/common/LoadingStateKt*",
    "**/presentation/common/ErrorStateKt*",
    "**/presentation/common/EmptyStateKt*",
    "**/presentation/common/NetworkImageKt*",
    "**/presentation/common/ShimmerVenueCardKt*",
    "**/presentation/common/LocationFormatterKt*",
    "**/presentation/venues/components/**",
    "**/presentation/venues/VenueListScreenKt*",
    "**/presentation/theme/**",

    // Utilities (vendored code from Wolt - BlurHashDecoder)
    "**/util/**"
)

// Task to generate JaCoCo coverage report
val jacocoTestReport = tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    sourceDirectories.setFrom(files("src/main/java"))
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.map { it.dir("tmp/kotlin-classes/debug") }) {
            exclude(coverageExclusions)
        }
    )
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        }
    )
}

// Task to verify coverage meets minimum thresholds
val jacocoCoverageVerification = tasks.register<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn("testDebugUnitTest")

    classDirectories.setFrom(
        fileTree(layout.buildDirectory.map { it.dir("tmp/kotlin-classes/debug") }) {
            exclude(coverageExclusions)
        }
    )
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        }
    )

    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal() // 80% overall coverage
            }
        }

        rule {
            element = "CLASS"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.70".toBigDecimal() // 70% per class minimum
            }

            // Exclude classes with no business logic
            excludes = listOf(
                "*.dto.*",
                "*.BuildConfig",
                "*.*_Factory",
                "*.*_HiltModules*",
                "*.RestoFinderApplication"
            )
        }
    }
}

// Task to generate instrumented test coverage (UI components)
val jacocoInstrumentedTestReport = tasks.register<JacocoReport>("jacocoInstrumentedTestReport") {
    dependsOn("createDebugAndroidTestCoverageReport")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    sourceDirectories.setFrom(files("src/main/java"))
    classDirectories.setFrom(
        fileTree(layout.buildDirectory.map { it.dir("tmp/kotlin-classes/debug") }) {
            // Don't exclude UI components for instrumented tests - they're being tested!
            exclude(listOf(
                "**/R.class",
                "**/R$*.class",
                "**/BuildConfig.*",
                "**/Manifest*.*",
                "**/*Test*.*",
                "**/*_HiltModules*",
                "**/*_Factory*",
                "**/*_MembersInjector*",
                "**/Hilt_*",
                "**/*_Impl*",
                "**/*Module_*",
                "**/*$$*.class",
                "**/*ComposableSingletons*",
                "**/dto/**",
                "**/RestoFinderApplication*"
            ))
        }
    )
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("outputs/code_coverage/debugAndroidTest/connected/**/*.ec")
        }
    )
}

// Combined task for unit tests + coverage
tasks.register("testWithCoverage") {
    group = "verification"
    description = "Run unit tests with code coverage report and verification"

    dependsOn(
        "testDebugUnitTest",
        "jacocoTestReport",
        "jacocoTestCoverageVerification"
    )

    tasks.findByName("jacocoTestReport")?.mustRunAfter("testDebugUnitTest")
    tasks.findByName("jacocoTestCoverageVerification")?.mustRunAfter("jacocoTestReport")
}

// Combined task for instrumented tests + coverage
tasks.register("instrumentedTestWithCoverage") {
    group = "verification"
    description = "Run instrumented tests with code coverage report (UI components)"

    dependsOn(
        "createDebugAndroidTestCoverageReport",
        "jacocoInstrumentedTestReport"
    )

    tasks.findByName("jacocoInstrumentedTestReport")?.mustRunAfter("createDebugAndroidTestCoverageReport")
}

// Combined task for ALL tests + coverage
tasks.register("allTestsWithCoverage") {
    group = "verification"
    description = "Run all tests (unit + instrumented) with code coverage reports"

    dependsOn(
        "testWithCoverage",
        "instrumentedTestWithCoverage"
    )

    tasks.findByName("instrumentedTestWithCoverage")?.mustRunAfter("testWithCoverage")
}

// Print coverage report locations after generation
jacocoTestReport.configure {
    doLast {
        val reportDir = layout.buildDirectory.get().asFile
        println("\n✅ Unit Test Coverage Report (Business Logic):")
        println("   HTML: file://$reportDir/reports/jacoco/jacocoTestReport/html/index.html")
        println("   XML: $reportDir/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        println("   Note: UI components excluded - see instrumented test coverage")
    }
}

jacocoInstrumentedTestReport.configure {
    doLast {
        val reportDir = layout.buildDirectory.get().asFile
        println("\n✅ Instrumented Test Coverage Report (UI Components):")
        println("   HTML: file://$reportDir/reports/jacoco/jacocoInstrumentedTestReport/html/index.html")
        println("   XML: $reportDir/reports/jacoco/jacocoInstrumentedTestReport/jacocoInstrumentedTestReport.xml")
        println("   Note: Run './gradlew instrumentedTestWithCoverage' to generate")
    }
}
