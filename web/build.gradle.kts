import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

val releaseArtifact: String by project

plugins {
    kotlin("multiplatform")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "bobatea-web"
        browser {
            commonWebpackConfig {
                outputFileName = "bobatea-web.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        wasmJsMain.dependencies {
            implementation(project(":$releaseArtifact"))
            implementation(libs.kotlinx.coroutines)
            implementation(libs.mordant)
        }
    }
}
