plugins {
    kotlin("multiplatform") version "2.0.21"
}

kotlin {
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
            // Substituted by :bobatea from the root composite build
            implementation("io.github.dylanwatsonsoftware:bobatea")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            implementation("com.github.ajalt.mordant:mordant:2.6.0")
        }
    }
}
