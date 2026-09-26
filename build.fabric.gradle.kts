plugins {
    id("dev.kikugie.loom-back-compat")
    id("me.modmuss50.mod-publish-plugin")
    id("maven-publish")
}

val modernHud = stonecutter.eval(stonecutter.current.version, ">=1.21.11")
val minecraftVersion = stonecutter.current.version
val modVersion = property("mod.version") as String
val fabricMinecraftRange = property("mod.fabric_mc_range").toString()
val syrupLibraryVersion = "${property("deps.syrup_library")}+$minecraftVersion-fabric"
val syrupLibraryCoordinate = "net.syrupstudios:syrup_library:$syrupLibraryVersion"
val targetJavaVersion = when {
    stonecutter.eval(stonecutter.current.version, ">=26") -> 25
    stonecutter.eval(stonecutter.current.version, ">=1.20.5") -> 21
    else -> 17
}
val requiredJava = JavaVersion.toVersion(targetJavaVersion)

repositories {
    maven("https://maven.syrupstudios.net/releases/")
}

version = modVersion
group = property("mod.group") as String
base.archivesName = property("mod.id") as String

tasks.withType<AbstractArchiveTask>().configureEach {
    archiveVersion.set("$modVersion+$minecraftVersion-fabric")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    loomx.applyMojangMappings()
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    modImplementation(syrupLibraryCoordinate)
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }
    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run")
        jvmArguments.add("-Dmixin.debug.export=true")
    }
}

if (modernHud) {
    sourceSets.main { java.exclude("net/syrupstudios/colorfularmorbar/mixin/**") }
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
    toolchain {
        vendor = JvmVendorSpec.ADOPTIUM
        languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }
}

tasks.processResources {
    val packFormat = project.property("deps.resource_pack_format")
    val packFormatMinor = project.findProperty("deps.resource_pack_minor")
    val props = mapOf(
        "version" to modVersion,
        "mc" to fabricMinecraftRange,
        "packVersions" to if (modernHud) {
            val version = if (packFormatMinor == null) "$packFormat, 0" else "$packFormat, $packFormatMinor"
            val maxVersion = if (packFormatMinor == null) "$packFormat" else "[$version]"
            "\"min_format\": [$version],\n    \"max_format\": $maxVersion,"
        } else {
            "\"pack_format\": $packFormat,"
        },
        "modName" to project.property("mod.name"),
        "modId" to project.property("mod.id"),
        "modDescription" to project.property("mod.description"),
        "authors" to project.property("mod.authors"),
        "license" to project.property("mod.license"),
        "homepage" to project.property("mod.homepage"),
        "issues" to project.property("mod.issues"),
        "sources" to project.property("mod.sources"),
        "fl" to project.property("deps.fabric_loader"),
        "fapi" to project.property("deps.fabric_api"),
        "syrupLibrary" to project.property("deps.syrup_library")
    )

    inputs.properties(props)
    exclude("META-INF/mods.toml", "META-INF/neoforge.mods.toml")
    filesMatching("fabric.mod.json") { expand(props) }
    filesMatching("pack.mcmeta") { expand(props) }

    if (modernHud) {
        exclude("*.mixins.json")
    } else {
        val mixinJavaCompatibility = "JAVA_$targetJavaVersion"
        filesMatching("*.mixins.json") {
            expand("java" to mixinJavaCompatibility, "refmapLine" to "")
        }
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
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
val archiveVersion = "$modVersion+$minecraftVersion-fabric"

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
    file = loomx.modJar.flatMap { it.archiveFile }
    dryRun = publishDryRun
    version = archiveVersion
    displayName = "${property("mod.name")} ${property("mod.version")} - Fabric $minecraftVersion"
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    type = when (property("publish.release_type").toString().lowercase()) {
        "stable" -> STABLE
        "beta" -> BETA
        "alpha" -> ALPHA
        else -> error("publish.release_type must be stable, beta, or alpha")
    }
    modLoaders.add("fabric")

    curseforge {
        projectId = property("publish.curseforge").toString()
        accessToken = curseForgeToken
        compatibleVersions.forEach { minecraftVersions.add(it) }
        client = true
        server = false
        requires("fabric-api")
        requires("syrup-library")
    }
    modrinth {
        projectId = property("publish.modrinth").toString()
        accessToken = modrinthToken
        compatibleVersions.forEach { minecraftVersions.add(it) }
        environment = CLIENT_ONLY
        requires("fabric-api")
        requires("syrup-library")
    }
}
