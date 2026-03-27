pluginManagement.repositories {
    gradlePluginPortal()
    mavenCentral()
}
dependencyResolutionManagement.repositories.mavenCentral()

rootProject.name = "bobatea"

include("bobatea")
include("sample")
include("website")

// web uses Kotlin 2.0 for WASM stability — kept as a separate composite build.
// Excluded on JitPack (glibc 2.17 / no WASM support needed there).
if (System.getenv("JITPACK") == null) {
    includeBuild("web") {
        dependencySubstitution {
            substitute(module("io.github.dylanwatsonsoftware:bobatea"))
                .using(project(":bobatea"))
        }
    }
}
