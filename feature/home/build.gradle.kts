plugins {
    id("goose.android.library")
    id("goose.android.compose")
    id("goose.android.hilt")
}

android {
    namespace = "little.goose.home"
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.google.android.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.compose.calendar)
    implementation(project(":core:design-system"))
    implementation(project(":core:common"))
    implementation(project(":feature:search"))
    implementation(project(":feature:memorial"))
    implementation(project(":feature:schedule"))
    implementation(project(":feature:note"))
    implementation(project(":feature:account"))
    implementation(project(":feature:settings"))
    implementation(project(":core:ui"))
}