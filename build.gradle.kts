plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kapt) apply false
    alias(libs.plugins.kotlinSerialization) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
