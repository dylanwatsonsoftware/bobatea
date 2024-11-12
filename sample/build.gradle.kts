val releaseArtifact: String by project

plugins {
    kotlin("multiplatform") version libs.versions.kotlin
    application
}

application.mainClass.set("com.example.App")

dependencies {
    implementation(project(":$releaseArtifact"))
}

kotlin {
    jvm()
    macosX64()
}
