// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false
    alias(libs.plugins.ksp) apply false
    // Use plugin aliases defined in gradle/libs.versions.toml for consistency
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
}