plugins {
    id("java")
}

dependencies {
    implementation(project(":pixtill-core"))
    compileOnly("net.md-5:bungeecord-api:1.21-R0.4")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("bungee.yml") {
        expand(mapOf("version" to project.version))
    }
}
