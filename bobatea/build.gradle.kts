import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            implementation(libs.mordant)
            implementation(libs.mordant.markdown)
        }
        jvmTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation(libs.truth)
        }
    }
}
