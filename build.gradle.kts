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
}

allprojects {
    apply<JavaLibraryPlugin>()
    apply<MavenPublishPlugin>()
    apply<IdeaExtPlugin>()
    apply<IndraPlugin>()

    group = "dev.booky"
    version = "1.1.0-SNAPSHOT"

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
}

subprojects {
    tasks.withType<Jar> {
        destinationDirectory = rootProject.tasks.jar.map { it.destinationDirectory }.get()
    }
}

dependencies {
    api(projects.cloudCoreCommon)
    compileOnly(libs.paper.api)

    // command library
    compileOnlyApi(libs.commandapi.core)
    compileOnlyApi(libs.brigadier) // required for cmd api to compile

    // metrics
    implementation(libs.bstats.bukkit)
}

bukkit {
    main = "$group.cloudcore.CloudCoreMain"
    apiVersion = "1.20.5"
    authors = listOf("booky10")
    website = "https://github.com/CloudCraftProjects/CloudCore"
    depend = listOf("CommandAPI")
}

tasks {
    runServer {
        minecraftVersion("1.20.6")

        downloadPlugins {
            hangar("CommandAPI", libs.versions.commandapi.get())
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
