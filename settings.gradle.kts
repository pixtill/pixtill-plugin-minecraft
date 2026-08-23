plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "pixtill-plugin-minecraft"

include(":pixtill-core")
include(":pixtill-bukkit")
include(":pixtill-bungee")
include(":pixtill-velocity")
include(":pixtill-all")
