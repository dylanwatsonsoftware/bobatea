import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/dylanwatsonsoftware/bobatea")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: ""
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}

kotlin {
    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        // browser() is needed locally and on GitHub CI so that dependent projects
        // (e.g. :web) can resolve this as a browser-compatible wasmJs dependency.
        // Skipped on JitPack because its glibc 2.17 can't run any modern Node.js binary.
        if (System.getenv("JITPACK") == null) {
            browser()
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
            api(libs.mordant)
        }
        jvmTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation(libs.truth)
        }
    }
}
