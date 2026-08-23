plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.1"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":pixtill-core"))
    implementation(project(":pixtill-bukkit"))
    implementation(project(":pixtill-bungee"))
    implementation(project(":pixtill-velocity"))
}

tasks.shadowJar {
    archiveFileName.set("pixtill-${project.version}.jar")

    relocate("okhttp3", "pl.pixtill.plugin.libs.okhttp3")
    relocate("okio", "pl.pixtill.plugin.libs.okio")
    relocate("kotlin", "pl.pixtill.plugin.libs.kotlin")
    relocate("com.google.gson", "pl.pixtill.plugin.libs.gson")
    relocate("org.yaml.snakeyaml", "pl.pixtill.plugin.libs.snakeyaml")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
