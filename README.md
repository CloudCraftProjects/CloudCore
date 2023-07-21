# CloudCore

Minecraft paper library plugin which most new plugins of CloudCraft depend on.

## Content

- Configurate yaml based configuration loaded (+ some default serializers)
- Block bounding box (with a defined world)
- Simple adventure translation loader
- EntityPosition, extends Paper's new (experimental) Position API with yaw and pitch

## Download

https://nightly.link/CloudCraftProjects/CloudCore/workflows/build/master/CloudCore-Artifacts.zip

## Usage

Requires `githubUsername` and `githubPassword` (classic GitHub token with package registry access) to be set
in `~/.gradle/gradle.properties`. Then add the following to your `build.gradle.kts`:

```kotlin
repositories {
    maven("https://maven.pkg.github.com/CloudCraftProjects/*/") {
        name = "github"
        credentials(PasswordCredentials::class.java)
    }
}

dependencies {
    compileOnly("dev.booky:cloudcore:{VERSION}")
}
```

`{VERSION}` has to be replaced with the latest version from the latest available package.

## License

Licensed under GPL-3.0, see [LICENSE](./LICENSE) for further information.
