plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.148" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.148" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.1" apply false
}

stonecutter active "1.21.1-fabric"

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
