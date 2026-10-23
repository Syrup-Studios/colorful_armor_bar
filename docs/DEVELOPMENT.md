# Build and IDE information

## Supported build targets

The Stonecutter workspace currently produces these client artifacts:

- Minecraft 1.20.1: Fabric and Forge
- Minecraft 1.21.1: Fabric and NeoForge
- Minecraft 1.21.11: Fabric and NeoForge
- Minecraft 26.2: Fabric and NeoForge
- Minecraft 26.3: Fabric and NeoForge

Run `./gradlew build` to compile all registered targets, or address a specific
target directly, for example `./gradlew :1.21.1-neoforge:build`.

To change the shared Stonecutter source target for IDE work, run for example
`./gradlew "Set active project to 1.21.1-fabric"`.

The project selects Java 21 for the Gradle daemon. The Minecraft 1.20.1
target still uses Java 17.
