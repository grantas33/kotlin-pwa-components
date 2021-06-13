plugins {
    id("org.jetbrains.kotlin.js") version "1.5.10"
    id("org.jetbrains.dokka") version "1.4.32"
    `maven-publish`
}

group = "io.github.grantas33"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://kotlin.bintray.com/kotlin-js-wrappers/")
    mavenCentral()
}

kotlin {
    js {
        browser {}
    }
}

tasks.dokkaHtml.configure {
    dokkaSourceSets {
        named("main") {
            includes.from("README.md")
        }
    }
}

configure<PublishingExtension> {
    publications {
        create<MavenPublication>("kotlin") {
            from(components["kotlin"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
            artifact(tasks.getByName<Zip>("jsSourcesJar"))
        }
    }
}

val implementation by configurations

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.5.0")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:17.0.2-pre.204-kotlin-1.5.0")
}

