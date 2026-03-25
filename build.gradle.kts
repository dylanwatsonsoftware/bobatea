import com.vanniktech.maven.publish.JavadocJar
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.MavenPublishBasePlugin
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val developerId: String by project
val developerName: String by project
val developerUrl: String by project
val releaseGroup: String by project
val releaseArtifact: String by project
val releaseVersion: String by project
val releaseDescription: String by project
val releaseUrl: String by project

val jreVersion = JavaLanguageVersion.of(libs.versions.jre.get())

plugins {
    kotlin("jvm") version libs.versions.kotlin apply false
    kotlin("multiplatform") version libs.versions.kotlin apply false
    alias(libs.plugins.dokka)
    alias(libs.plugins.maven.publish) apply false
}

allprojects {
    group = releaseGroup
    version = releaseVersion
}

subprojects {
    plugins.withType<MavenPublishBasePlugin> {
        configure<MavenPublishBaseExtension> {
            configure(KotlinJvm(JavadocJar.Dokka("dokkaJavadoc")))
            //publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            //signAllPublications()
            pom {
                name.set(project.name)
                description.set(releaseDescription)
                url.set(releaseUrl)
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set(developerId)
                        name.set(developerName)
                        url.set(developerUrl)
                    }
                }
                scm {
                    url.set(releaseUrl)
                    connection.set("scm:git:https://github.com/$developerId/$releaseArtifact.git")
                    developerConnection.set("scm:git:ssh://git@github.com/$developerId/$releaseArtifact.git")
                }
            }
        }
    }

    tasks {
        withType<JavaCompile>().configureEach {
            options.release = jreVersion.asInt()
        }
        withType<KotlinCompile>().configureEach {
            compilerOptions.jvmTarget
                .set(JvmTarget.fromTarget(JavaVersion.toVersion(jreVersion).toString()))
        }
    }
}

tasks {
    dokkaHtmlMultiModule {
        outputDirectory.set(layout.buildDirectory.dir("dokka/dokka/"))
    }
}

// JitPack's environment only has glibc 2.17 (CentOS 7).
// Node.js 18+ requires glibc 2.25+; Node.js 16.x is the last series built against glibc 2.17.
plugins.withType<NodeJsRootPlugin> {
    the<NodeJsRootExtension>().nodeVersion = "16.20.2"
}
