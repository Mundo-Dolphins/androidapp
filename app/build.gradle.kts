import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.crashlytics)
    alias(libs.plugins.google.services)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.compose)
    id("jacoco") // Added
    // Static analysis plugins
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

// Import types to avoid fully-qualified names and remove warnings

android {
    namespace = "es.mundodolphins.app"
    compileSdk = 36

    defaultConfig {
        applicationId = "es.mundodolphins.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 12
        versionName = "2.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // Enable resources in unit tests so Robolectric can access strings, layouts, etc.
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        getByName("debug") {
            // This will access/create the debug build type
            enableUnitTestCoverage = true // Ensures coverage is enabled for debug builds
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Core + AndroidX
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.exoplayer.dash)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.config)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)
    implementation(libs.hilt.android)

    // Networking
    implementation(libs.converter.gson)
    implementation(libs.retrofit)

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.room.runtime)
    testImplementation(libs.junit.jupiter)

    // Annotation processors
    annotationProcessor(libs.androidx.room.compiler)

    // KSP
    ksp(libs.androidx.room.compiler)
    ksp(libs.hilt.android.compiler)

    // Markwon
    implementation(libs.markwon)

    // Test
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.room.testing)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.robolectric)
    testImplementation(libs.truth)
    testImplementation(libs.awaitility.kotlin)
    // AssertJ for fluent assertions
    testImplementation(libs.assertj.core)
    // Compose UI testing in unit tests (Robolectric + Compose)
    testImplementation(libs.ui.test.junit4)

    // Android Test
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.rules)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
}

// Configure Jacoco for Test tasks
tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
        // (Removed destinationFile assignment to avoid static analysis error in this environment)
    }
    // Ensure tests run on Java 17 toolchain to match MockK/Robolectric expectations
    javaLauncher.set(
        javaToolchains.launcherFor {
            languageVersion.set(
                org.gradle.jvm.toolchain.JavaLanguageVersion
                    .of(17),
            )
        },
    )
}

// Register Jacoco report task for debug unit tests
tasks.register<JacocoReport>("testDebugUnitTestCoverage") {
    dependsOn("testDebugUnitTest") // Ensure tests run first
    group = "verification"
    description = "Generate Jacoco code coverage reports for the debug unit tests."

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false) // XML and HTML are usually sufficient
    }

    // Define file filter for excluding certain classes from coverage
    val fileFilter =
        listOf(
            "**/R.class",
            "**/R$*.class",
            "**/BuildConfig.*",
            "**/Manifest*.*",
            "**/*Test*.*", // Exclude test classes
            "android/**/*.*", // Exclude Android framework classes
            // Add other common exclusions (e.g., Dagger/Hilt, DataBinding, Models, DI modules)
            "**/databinding/*",
            "**/androidx/databinding/*",
            "**/BR.class",
            "**/*_HiltModules*.*",
            "**/*_Factory*.*",
            "**/*_MembersInjector*.*",
            "**/Dagger*Component*.*",
            "**/*Module_*Factory.class",
            "**/model/**", // Example: Exclude data classes in 'model' package
            "**/di/**", // Example: Exclude DI modules
        )

    // Define source directories (adjust if your Kotlin sources are elsewhere)
    val mainSrcJava = "${project.projectDir}/src/main/java"
    val mainSrcKotlin = "${project.projectDir}/src/main/kotlin" // Common for Kotlin-only projects
    sourceDirectories.setFrom(files(mainSrcJava, mainSrcKotlin).filter { it.exists() })

    // Define class directories using layout.buildDirectory to avoid deprecated buildDir usage
    val debugTree =
        fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")) {
            exclude(fileFilter)
        }
    classDirectories.setFrom(files(debugTree))

    // Point directly to the .exec file generated by testDebugUnitTest
    executionData.setFrom(files(layout.buildDirectory.file("jacoco/testDebugUnitTest.exec")))
}

// Optional: Specify JaCoCo version (if not managed by a BOM or convention plugin)
// jacoco {
//     toolVersion = "0.8.11" // Example: Using a specific version
// }

// Configure detekt to use our custom configuration file
detekt {
    config.setFrom(files("${project.rootDir}/detekt.yml"))
    buildUponDefaultConfig = true
}
