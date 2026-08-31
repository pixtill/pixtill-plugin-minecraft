plugins {
    id("java")
}

dependencies {
    implementation(project(":pixtill-core"))
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("plugin.yml") {
        expand(mapOf("version" to project.version))
    }
}
