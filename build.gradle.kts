import org.jetbrains.changelog.date

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.8.0"

    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "1.3.1"
}

group = "com.stackspot"
version = System.getProperty("project_version")

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set("2022.1")
    type.set("IC") // Target IDE Platform

    plugins.set(
        listOf(
            "org.jetbrains.plugins.terminal",
            "com.intellij.gradle",
            "org.jetbrains.idea.maven",
        )
    )
}

// Configure Gradle Changelog Plugin - read more: https://github.com/JetBrains/gradle-changelog-plugin
changelog {
    val regex =
        Regex("""^v((0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\+([0-9a-zA-Z-]+(?:\.[0-9a-zA-Z-]+)*))?)${'$'}""")
    headerParserRegex.set(regex)
    header.set(provider { "[v${project.version}] - ${date()}" })

    unreleasedTerm.set("Releases")
    itemPrefix.set("*")
    version.set(project.version as String)

    groups.set(emptyList())
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("221")
        untilBuild.set("222.*")
    }

    buildSearchableOptions {
        enabled = false
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf(System.getenv("MARKETPLACE_CHANNEL")))
    }
}