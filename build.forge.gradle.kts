plugins {
    id("net.neoforged.moddev.legacyforge")
    id("neoforge-mutex")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

val mcVersion = stonecutter.current.version
val forgeVersion = property("deps.forge_version") as String
val modVersion = property("mod.version") as String
val forgeMinecraftRange = property("mod.forge_mc_range").toString()
val targetJavaVersion = 17
val syrupLibraryVersion = "${property("deps.syrup_library")}+$mcVersion-forge"
val syrupLibraryCoordinate = "net.syrupstudios:syrup_library:$syrupLibraryVersion"

version = modVersion
group = property("mod.group") as String
base.archivesName = property("mod.id") as String

repositories {
    maven("https://maven.syrupstudios.net/releases/")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    archiveVersion.set("$modVersion+$mcVersion-forge")
}

legacyForge {
    setVersion("$mcVersion-$forgeVersion")
    runs {
        create("client") {
            client()
            gameDirectory = rootProject.file("run")
        }
        create("server") {
            server()
            gameDirectory = rootProject.file("run")
        }
    }
    mods.create(property("mod.id") as String) { sourceSet(sourceSets.main.get()) }
}

dependencies {
    implementation(syrupLibraryCoordinate)
}

// ModDevGradle legacy reobf variants drop runtime dependencies (upstream issue #227).
configurations.named("reobfRuntimeElements") {
    extendsFrom(configurations.runtimeElements.get())
}
(components["java"] as AdhocComponentWithVariants).withVariantsFromConfiguration(
    configurations.getByName("reobfRuntimeElements")
) {
    mapToMavenScope("runtime")
}

sourceSets.main {
    java.exclude("net/syrupstudios/colorfularmorbar/mixin/**")
}

java {
    withSourcesJar()
    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.processResources {
    val props = mapOf(
        "version" to modVersion,
        "mc" to forgeMinecraftRange,
        "packVersions" to "\"pack_format\": ${project.property("deps.resource_pack_format")},",
        "forge" to forgeVersion,
        "modName" to project.property("mod.name"),
        "modId" to project.property("mod.id"),
        "modDescription" to project.property("mod.description"),
        "authors" to project.property("mod.authors"),
        "license" to project.property("mod.license"),
        "syrupLibrary" to project.property("deps.syrup_library")
    )
    inputs.properties(props)
    filesMatching("META-INF/mods.toml") { expand(props) }
    filesMatching("pack.mcmeta") { expand(props) }
    exclude("fabric.mod.json", "META-INF/neoforge.mods.toml", "*.mixins.json")
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.named("jar"), tasks.named("sourcesJar"))
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}

val compatibleVersions = stonecutter.properties.rawOrNull("mod.mc_releases")
    ?.asList().orEmpty().map { it.toString() }
val curseForgeToken = providers.gradleProperty("publish.curseforge_token")
    .orElse(providers.environmentVariable("CURSEFORGE_TOKEN"))
val modrinthToken = providers.gradleProperty("publish.modrinth_token")
    .orElse(providers.environmentVariable("MODRINTH_TOKEN"))
val publishDryRun = providers.gradleProperty("publish.dry_run")
    .map(String::toBoolean)
    .orElse(curseForgeToken.isPresent.not() || modrinthToken.isPresent.not())
val archiveVersion = "$modVersion+$mcVersion-forge"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = base.archivesName.get()
            version = archiveVersion
        }
    }
    repositories {
        maven {
            name = "syrupStudios"
            url = uri("https://maven.syrupstudios.net/releases/")
            credentials(PasswordCredentials::class)
        }
    }
}

publishMods {
    file = tasks.named<Jar>("jar").flatMap { it.archiveFile }
    dryRun = publishDryRun
    version = archiveVersion
    displayName = "${property("mod.name")} ${property("mod.version")} - Forge $mcVersion"
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    type = when (property("publish.release_type").toString().lowercase()) {
        "stable" -> STABLE
        "beta" -> BETA
        "alpha" -> ALPHA
        else -> error("publish.release_type must be stable, beta, or alpha")
    }
    modLoaders.add("forge")

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseForgeToken
        compatibleVersions.forEach { minecraftVersions.add(it) }
        client = true
        server = false
        requires("syrup-library")
    }
    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken
        compatibleVersions.forEach { minecraftVersions.add(it) }
        environment = CLIENT_ONLY
        requires("syrup-library")
    }
}
