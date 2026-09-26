plugins {
    id("net.neoforged.moddev")
    id("neoforge-mutex")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

val minecraftVersion = stonecutter.current.version
val neoForgeVersion = property("deps.neoforge_version") as String
val modVersion = property("mod.version") as String
val neoForgeMinecraftRange = property("mod.neoforge_mc_range").toString()
val modernHud = stonecutter.eval(stonecutter.current.version, ">=1.21.11")
val targetJavaVersion = if (stonecutter.eval(stonecutter.current.version, ">=26")) 25 else 21
val syrupLibraryVersion = "${property("deps.syrup_library")}+$minecraftVersion-neoforge"
val syrupLibraryCoordinate = "net.syrupstudios:syrup_library:$syrupLibraryVersion"

version = modVersion
group = property("mod.group") as String
base.archivesName = property("mod.id") as String

repositories {
    maven("https://maven.syrupstudios.net/releases/")
}

tasks.withType<AbstractArchiveTask>().configureEach {
    archiveVersion.set("$modVersion+$minecraftVersion-neoforge")
}

neoForge {
    version = neoForgeVersion
    runs {
        create("client") { client(); gameDirectory = rootProject.file("run") }
        create("server") { server(); gameDirectory = rootProject.file("run") }
    }
    mods.create(property("mod.id") as String) { sourceSet(sourceSets.main.get()) }
}

dependencies {
    implementation(syrupLibraryCoordinate)
}

if (modernHud) {
    sourceSets.main { java.exclude("net/syrupstudios/colorfularmorbar/mixin/**") }
}

java {
    withSourcesJar()
    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
    sourceCompatibility = JavaVersion.toVersion(targetJavaVersion)
    targetCompatibility = JavaVersion.toVersion(targetJavaVersion)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.processResources {
    val packFormat = project.property("deps.resource_pack_format")
    val packFormatMinor = project.findProperty("deps.resource_pack_minor")
    val props = mapOf(
        "version" to modVersion,
        "mc" to neoForgeMinecraftRange,
        "packVersions" to if (modernHud) {
            val version = if (packFormatMinor == null) "$packFormat, 0" else "$packFormat, $packFormatMinor"
            val maxVersion = if (packFormatMinor == null) "$packFormat" else "[$version]"
            "\"min_format\": [$version],\n    \"max_format\": $maxVersion,"
        } else {
            "\"pack_format\": $packFormat,"
        },
        "neoforge" to neoForgeVersion,
        "modName" to project.property("mod.name"),
        "modId" to project.property("mod.id"),
        "modDescription" to project.property("mod.description"),
        "authors" to project.property("mod.authors"),
        "license" to project.property("mod.license"),
        "syrupLibrary" to project.property("deps.syrup_library"),
        "mixinConfig" to if (modernHud) "" else "[[mixins]]\nconfig=\"${project.property("mod.id")}.mixins.json\""
    )
    inputs.properties(props)
    filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
    filesMatching("pack.mcmeta") { expand(props) }
    if (modernHud) {
        exclude("*.mixins.json")
    } else {
        filesMatching("*.mixins.json") {
            expand("java" to "JAVA_21", "refmapLine" to "")
        }
    }
    exclude("fabric.mod.json", "META-INF/mods.toml")
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
val archiveVersion = "$modVersion+$minecraftVersion-neoforge"

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
    displayName = "${property("mod.name")} ${property("mod.version")} - NeoForge $minecraftVersion"
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    type = when (property("publish.release_type").toString().lowercase()) {
        "stable" -> STABLE
        "beta" -> BETA
        "alpha" -> ALPHA
        else -> error("publish.release_type must be stable, beta, or alpha")
    }
    modLoaders.add("neoforge")

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
