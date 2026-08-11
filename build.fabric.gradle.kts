import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.17.14" apply false
    id("net.fabricmc.fabric-loom") version "1.17.14" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
    id("maven-publish")
}

val remappedMinecraft = stonecutter.eval(stonecutter.current.version, "<26")
val modernHud = stonecutter.eval(stonecutter.current.version, ">=1.21.11")
val minecraftVersion = property("deps.minecraft") as String
val modVersion = property("mod.version") as String
val targetJavaVersion = when {
    stonecutter.eval(stonecutter.current.version, ">=26") -> 25
    stonecutter.eval(stonecutter.current.version, ">=1.20.5") -> 21
    else -> 17
}
val requiredJava = JavaVersion.toVersion(targetJavaVersion)

apply(plugin = if (remappedMinecraft) "net.fabricmc.fabric-loom-remap" else "net.fabricmc.fabric-loom")

repositories {
    maven("https://maven.syrupstudios.net/releases/")
}

version = modVersion
group = property("mod.group") as String
base.archivesName = property("mod.id") as String

tasks.withType<AbstractArchiveTask>().configureEach {
    archiveVersion.set("$modVersion+$minecraftVersion-fabric")
}

val loomExtension = extensions.getByType<LoomGradleExtensionAPI>()

dependencies {
    add("minecraft", "com.mojang:minecraft:$minecraftVersion")
    if (remappedMinecraft) add("mappings", loomExtension.officialMojangMappings())

    val modConfiguration = if (remappedMinecraft) "modImplementation" else "implementation"
    add(modConfiguration, "net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    add(modConfiguration, "net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    add(modConfiguration, "net.syrupstudios:syrup_library:${property("deps.syrup_library")}+$minecraftVersion-fabric")
}

loomExtension.apply {
    fabricModJsonPath.set(rootProject.file("src/main/resources/fabric.mod.json"))
    if (remappedMinecraft) {
        decompilerOptions.named("vineflower") {
            options.put("mark-corresponding-synthetics", "1")
        }
    }
    runConfigs.configureEach { runDir = "run" }
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

val fabricMetadataSource = rootProject.file("src/main/resources/fabric.mod.json")
val generatedFabricMetadata = layout.buildDirectory.file("generated/fabricMetadata/fabric.mod.generated.json")
val generateFabricMetadata = tasks.register("generateFabricMetadata") {
    inputs.file(fabricMetadataSource)
    inputs.property("modernHud", modernHud)
    outputs.file(generatedFabricMetadata)
    doLast {
        @Suppress("UNCHECKED_CAST")
        val metadata = JsonSlurper().parse(fabricMetadataSource) as MutableMap<String, Any?>
        if (modernHud) metadata.remove("mixins")

        generatedFabricMetadata.get().asFile.apply {
            parentFile.mkdirs()
            writeText(JsonOutput.prettyPrint(JsonOutput.toJson(metadata)) + "\n")
        }
    }
}

tasks.processResources {
    val packFormat = project.property("deps.resource_pack_format")
    val props = mapOf(
        "version" to project.version,
        "mc" to minecraftVersion,
        "packVersions" to if (modernHud) {
            "\"min_format\": [$packFormat, 0],\n    \"max_format\": $packFormat,"
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

    dependsOn(generateFabricMetadata)
    inputs.properties(props)
    exclude("fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml")
    from(generatedFabricMetadata) {
        rename { "fabric.mod.json" }
        expand(props)
    }
    filesMatching("pack.mcmeta") { expand(props) }

    if (modernHud) {
        exclude("*.mixins.json")
    } else {
        filesMatching("*.mixins.json") {
            expand("java" to "JAVA_$targetJavaVersion", "refmapLine" to "")
        }
    }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    val productionJar = if (remappedMinecraft) "remapJar" else "jar"
    val sourceJar = if (remappedMinecraft) "remapSourcesJar" else "sourcesJar"
    from(tasks.named(productionJar), tasks.named(sourceJar))
    into(rootProject.layout.buildDirectory.file("libs/${project.property("mod.version")}"))
    dependsOn("build")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

apply(from = rootProject.file("gradle/platform-publishing.gradle"))
