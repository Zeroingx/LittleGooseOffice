plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "little.goose.memorial"
}

dependencies {
    implementation(libs.androidx.appcompat)

    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":core:ui"))

    // Room database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.constraintLayout.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

}