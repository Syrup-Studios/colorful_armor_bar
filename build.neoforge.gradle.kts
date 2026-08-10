plugins {
    id("net.neoforged.moddev") version "2.0.137"
    id("maven-publish")
}

val minecraftVersion = property("deps.minecraft") as String
val neoForgeVersion = property("deps.neoforge_version") as String
val modVersion = property("mod.version") as String
val modernHud = stonecutter.eval(stonecutter.current.version, ">=1.21.11")
val targetJavaVersion = if (stonecutter.eval(stonecutter.current.version, ">=26")) 25 else 21

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
        create("client") { client(); gameDirectory = project.file("run") }
    }
    mods.create(property("mod.id") as String) { sourceSet(sourceSets.main.get()) }
}

dependencies {
    implementation("net.syrupstudios:syrup_library:${property("deps.syrup_library")}+$minecraftVersion-neoforge")
}

if (modernHud) {
    sourceSets.main { java.exclude("net/syrupstudios/colorfularmorbar/mixin/**") }
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
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
