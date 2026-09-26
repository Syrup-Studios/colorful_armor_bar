plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.147" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.137" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.0" apply false
}

stonecutter active "26.3-fabric"

stonecutter.handlers {
    inherit("json5", "json")
}

stonecutter {
    parameters {
        val (version, loader) = current.project.split('-', limit = 2)
        properties { tags(version, loader) }
        constants.match(loader, "fabric", "forge", "neoforge")

        replacements {
            string {
                direction = eval(current.version, "<1.21.11")
                replace(
                    "\"depends\": {",
                    "\"mixins\": [\n\t\t\"${'$'}{modId}.mixins.json\"\n\t],\n\t\"depends\": {"
                )
            }
            string {
                direction = eval(current.version, ">=1.21.11-rc2")
                replace("ResourceLocation", "Identifier")
            }
            string {
                direction = eval(current.version, ">=26.1")
                replace("GuiGraphics", "GuiGraphicsExtractor")
            }
        }
    }
}
