import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.modmuss50.mpp.MppPlugin
import me.modmuss50.mpp.PublishModTask
import me.modmuss50.mpp.ModPublishExtension

plugins {
    alias(libs.plugins.pluginyml.bukkit) apply false
    alias(libs.plugins.run.paper) apply false
    alias(libs.plugins.run.velocity) apply false
    alias(libs.plugins.shadow) apply false

    alias(libs.plugins.publishing)
}

val repositoryName = "CloudCraftProjects/CloudCore"

allprojects {
    group = "dev.booky"
}

publishMods {
    changelog = "See https://github.com/$repositoryName/releases/tag/v${project.version}"
    type = if (project.version.toString().endsWith("-SNAPSHOT")) BETA else STABLE
    dryRun = !hasProperty("noDryPublish")

    github {
        accessToken = providers.environmentVariable("GITHUB_API_TOKEN")
            .orElse(providers.gradleProperty("ccGithubToken"))

        displayName = "${rootProject.name} v${project.version}"

        repository = repositoryName
        commitish = "master"
        tagName = "v${project.version}"
    }
}

subprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()

    repositories {
        maven("https://repo.cloudcraftmc.de/public/")
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
            vendor = JvmVendorSpec.ADOPTIUM
        }
    }

    configure<PublishingExtension> {
        publications.create<MavenPublication>("maven") {
            artifactId = "${rootProject.name}-${project.name}".lowercase()
            from(components["java"])
        }
        repositories.maven("https://repo.cloudcraftmc.de/releases") {
            name = "horreo"
            credentials(PasswordCredentials::class.java)
        }
    }

    tasks.withType<Jar> {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
        archiveBaseName = "${rootProject.name}-${project.name}".lowercase()
    }

    if (project.projectDir.name != "common") {
        apply<ShadowPlugin>()
        apply<MppPlugin>()

        configure<ModPublishExtension> {
            file = tasks.named<ShadowJar>("shadowJar").flatMap { it.archiveFile }.get()
            changelog = "See https://github.com/$repositoryName/releases/tag/v${project.version}"
            type = if (project.version.toString().endsWith("-SNAPSHOT")) BETA else STABLE
            additionalFiles.from(tasks.named<Jar>("sourcesJar").flatMap { it.archiveFile }.get())
            dryRun = !hasProperty("noDryPublish")

            github {
                accessToken = providers.environmentVariable("GITHUB_API_TOKEN")
                    .orElse(providers.gradleProperty("ccGithubToken"))

                displayName = "${rootProject.name} v${project.version}"

                repository = repositoryName
                commitish = "master"
                tagName = "v${project.version}"

                parent(rootProject.tasks.named("publishGithub"))
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
            dependsOn(tasks.named("shadowJar"))
            dependsOn(tasks.named("sourcesJar"))
        }
    }
}

tasks.register<Delete>("clean") {
    delete(project.layout.buildDirectory)
}
