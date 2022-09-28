import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.changelog.date

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("jacoco")
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("org.jetbrains.intellij") version "1.8.0"
    id("org.sonarqube") version "3.4.0.2513"

    // Gradle Changelog Plugin
    id("org.jetbrains.changelog") version "1.3.1"
}

val projectVersion: String? = System.getProperty("project_version")

group = properties("pluginGroup")
version = if (projectVersion.isNullOrEmpty()) {
    properties("pluginVersion")
} else {
    projectVersion
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.13.1")
    testImplementation("io.kotest:kotest-assertions-core:5.4.2")
    testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType")) // Target IDE Platform

    plugins.set(
        properties("platformPlugins")
            .split(',')
            .map(String::trim)
            .filter(String::isNotEmpty)
    )
}

sonarqube {
    properties {
        property("sonar.projectName", "ide-intellij-plugin")
    }
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

    buildSearchableOptions {
        enabled = false
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
    }

    buildSearchableOptions {
        enabled = false
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels.set(listOf(System.getenv("MARKETPLACE_CHANNEL")))
    }

    test {
        useJUnitPlatform()
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
        }
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required.set(true)
        }
        classDirectories.setFrom(
            files(classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/com/stackspot/constants/**",
                        "**/com/stackspot/exceptions/**",
                        "**/com/stackspot/intellij/actions/**",
                        "**/com/stackspot/intellij/messaging/**",
                        "**/com/stackspot/intellij/ui/**"
                    )
                }
            })
        )
    }
}