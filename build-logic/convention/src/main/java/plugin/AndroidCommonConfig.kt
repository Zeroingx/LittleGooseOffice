package plugin

import AndroidConfigConventions
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType

fun Project.configureKotlin(
    commonExtension: CommonExtension<*, *, *, *>
) {
    with(commonExtension) {
        compileOptions {
            sourceCompatibility = AndroidConfigConventions.JAVA_VERSION
            targetCompatibility = AndroidConfigConventions.JAVA_VERSION
        }
        kotlinOptions {
            jvmTarget = AndroidConfigConventions.JAVA_VERSION.toString()
            freeCompilerArgs = freeCompilerArgs.toMutableList().apply {
                addAll(
                    listOf(
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                        "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                        "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
                    )
                )
                // open Kotlin context feature
                add("-Xcontext-receivers")
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = AndroidConfigConventions.JAVA_VERSION.toString()
        }
    }

}

fun CommonExtension<*, *, *, *>.configureAndroidCommon() {
    compileSdk = AndroidConfigConventions.COMPILE_SDK_VERSION

    defaultConfig {
        minSdk = AndroidConfigConventions.MIN_SDK_VERSION

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}