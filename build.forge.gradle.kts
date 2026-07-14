plugins {
    id("net.minecraftforge.gradle") version "6.0.46"
    id("maven-publish")
}

val minecraftVersion = property("deps.minecraft") as String
val forgeVersion = property("deps.forge_version") as String
val targetJavaVersion = 17

version = property("mod.version") as String
group = property("mod.group") as String
base.archivesName = "${property("mod.id")}-forge-$minecraftVersion"

minecraft {
    mappings("official", minecraftVersion)
    runs {
        create("client") {
            workingDirectory(project.file("run"))
            property("forge.logging.console.level", "info")
            mods.create(property("mod.id") as String) { source(sourceSets.main.get()) }
        }
    }
}

repositories { mavenCentral() }

dependencies {
    minecraft("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
}

sourceSets.main {
    java.exclude("net/syrupstudios/colorfularmorbar/mixin/**")
}

java {
    withSourcesJar()
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "mc" to minecraftVersion,
        "packFormat" to project.property("deps.resource_pack_format"),
        "forge" to forgeVersion,
        "modName" to project.property("mod.name"),
        "modId" to project.property("mod.id"),
        "modDescription" to project.property("mod.description"),
        "authors" to project.property("mod.authors"),
        "license" to project.property("mod.license")
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
