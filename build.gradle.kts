plugins {
    id("java-library")
    id("maven-publish")

    alias(libs.plugins.pluginyml.bukkit)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.shadow)
}

group = "dev.booky"
version = "1.0.1-SNAPSHOT"

val plugin: Configuration by configurations.creating {
    isTransitive = false
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)

    // command library
    compileOnlyApi(libs.commandapi.core)
    compileOnlyApi(libs.brigadier) // required for cmd api to compile

    // config library, included in paper
    compileOnlyApi(libs.configurate.yaml)

    // metrics
    implementation(libs.bstats.bukkit)

    // testserver dependency plugins
    plugin(libs.commandapi.plugin)
}

java {
    withSourcesJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = project.name.lowercase()
        from(components["java"])
    }
    repositories.maven("https://maven.pkg.github.com/CloudCraftProjects/CloudCore") {
        name = "github"
        credentials(PasswordCredentials::class.java)
    }
}

bukkit {
    main = "$group.cloudcore.CloudCoreMain"
    apiVersion = "1.20"
    authors = listOf("booky10")
    website = "https://github.com/CloudCraftProjects/CloudCore"
    depend = listOf("CommandAPI")
    load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.POSTWORLD
}

tasks {
    runServer {
        minecraftVersion("1.20.2")
        pluginJars.from(plugin.resolve())
    }

    shadowJar {
        relocate("org.bstats", "${project.group}.cloudcore.bstats")
    }

    assemble {
        dependsOn(shadowJar)
    }
}
