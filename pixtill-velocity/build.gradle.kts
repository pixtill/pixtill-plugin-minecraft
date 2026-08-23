plugins {
    id("java")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":pixtill-core"))
    compileOnly("com.velocitypowered:velocity-api:3.4.0")
}

tasks.processResources {
    inputs.property("version", project.version)
    filesMatching("velocity-plugin.json") {
        expand(mapOf("version" to project.version))
    }
}
