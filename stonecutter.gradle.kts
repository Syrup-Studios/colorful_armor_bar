plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active providers.gradleProperty("stonecutter.active")
    .orElse("1.21.1-fabric")
    .get()

stonecutter {
    parameters {
        val loader = node.metadata.project.substringAfterLast('-')
        constants.match(loader, "fabric", "forge", "neoforge")
        val modernHud = eval(current.version, ">=1.21.11")
        constants["modernneo"] = loader == "neoforge" && modernHud
        constants["legacyevent"] = loader == "forge" || (loader == "neoforge" && !modernHud)
    }
}
