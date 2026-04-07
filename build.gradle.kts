import net.fabricmc.loom.RunConfig

plugins {
    java
    alias(libs.plugins.voldeloom)
}

repositories {
    mavenCentral()
}

val modVersion: String by project
version = modVersion

val modGroup: String by project
group= modGroup

val modBaseName: String by project

val jdkVersion = 11
val compileTargetVersion = 6

dependencies {
    minecraft(libs.minecraft)
    forge(variantOf(libs.forge) { classifier("universal"); artifactType("zip") })
    mappings(variantOf(libs.forge) { classifier("src"); artifactType("zip") })

    compileOnly(libs.jsr305)

    // FIXME Configure shadow plugin for GSON
    implementation(libs.gson)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jdkVersion))
    }
}

tasks {
    compileJava {
        options.release.set(compileTargetVersion)
        // Show all compile errors
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "10000"))
    }
    compileTestJava {
        options.release.set(jdkVersion)
    }
    test {
        useJUnitPlatform()
    }
    jar {
        archiveBaseName.set(modBaseName)
    }
}

volde {
    runs {
        getByName<RunConfig>("client") {
            programArg("Player")
        }
    }
}

