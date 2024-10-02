import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import me.modmuss50.mpp.MppPlugin
import me.modmuss50.mpp.PublishModTask
import net.kyori.indra.IndraPlugin
import org.jetbrains.gradle.ext.IdeaExtPlugin

plugins {
    id("java-library")
    id("maven-publish")
    alias(libs.plugins.ideaext)

    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.run.velocity) apply false
    alias(libs.plugins.shadow)

    alias(libs.plugins.indra)
    alias(libs.plugins.blossom) apply false

    alias(libs.plugins.publishing)
}

allprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply<IdeaExtPlugin>()
    apply<IndraPlugin>()

    group = "dev.booky"

    repositories {
        maven("https://repo.cloudcraftmc.de/public/")
    }

    indra {
        javaVersions {
            target(21)
        }
    }

    java {
        toolchain {
            vendor = JvmVendorSpec.ADOPTIUM
        }
    }

    publishing {
        publications.create<MavenPublication>("maven") {
            artifactId = project.name.lowercase()
            from(components["java"])
        }
        repositories.maven("https://repo.cloudcraftmc.de/releases") {
            name = "horreo"
            credentials(PasswordCredentials::class.java)
        }
    }

    tasks.withType<Jar> {
        archiveBaseName = project.name
    }

    if (project != rootProject) {
        tasks.withType<Jar> {
            destinationDirectory = rootProject.tasks.jar.map { it.destinationDirectory }.get()
        }
    }

    if (project.projectDir.name != "common") {
        apply<ShadowPlugin>()
        apply<MppPlugin>()

        publishMods {
            val repositoryName = "CloudCraftProjects/CloudCore"
            file = tasks.shadowJar.flatMap { it.archiveFile }.get()
            changelog = "See https://github.com/$repositoryName/releases/tag/v${project.version}"
            type = if (project.version.toString().endsWith("-SNAPSHOT")) BETA else STABLE
            additionalFiles.from(tasks.sourcesJar.flatMap { it.archiveFile }.get())
            dryRun = !hasProperty("noDryPublish")

            github {
                accessToken = providers.environmentVariable("GITHUB_API_TOKEN")
                    .orElse(providers.gradleProperty("ccGithubToken"))

                displayName = "${rootProject.name} v${project.version}"

                repository = repositoryName
                commitish = "master"
                tagName = "v${project.version}"

                if (project != rootProject) {
                    parent(rootProject.tasks.named("publishGithub"))
                }
            }
            modrinth {
                accessToken = providers.environmentVariable("MODRINTH_API_TOKEN")
                    .orElse(providers.gradleProperty("ccModrinthToken"))

                val platformName = if (project == rootProject) "paper" else project.projectDir.name
                val fancyPlatformName = platformName.replaceFirstChar { it.titlecaseChar() }
                version = "${project.version}+$platformName"
                displayName = "${rootProject.name} $fancyPlatformName v${project.version}"
                modLoaders.add(platformName)

                projectId = "I9yBw5Kw"
                minecraftVersionRange {
                    start = rootProject.libs.versions.paper.get().split("-")[0]
                    end = "latest"
                }
            }
        }

        tasks.withType<PublishModTask> {
            dependsOn(tasks.shadowJar)
            dependsOn(tasks.sourcesJar)
        }
    }
}

dependencies {
    api(projects.cloudCoreCommon)
    compileOnly(libs.paper.api)

    // metrics
    implementation(libs.bstats.bukkit)
}

bukkit {
    main = "$group.cloudcore.CloudCoreBukkitMain"
    apiVersion = "1.20"
    authors = listOf("booky10")
    website = "https://github.com/CloudCraftProjects/CloudCore"
}

tasks {
    runServer {
        minecraftVersion(libs.versions.paper.map { it.split("-")[0] }.get())

        downloadPlugins {
            github(
                "PaperMC", "Debuggery",
                "v${libs.versions.debuggery.get()}",
                "debuggery-bukkit-${libs.versions.debuggery.get()}.jar"
            )
        }
    }

    shadowJar {
        relocate("org.bstats", "${project.group}.cloudcore.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }

    withType<Jar> {
        manifest.attributes(
            "paperweight-mappings-namespace" to "mojang"
        )
    }
}
