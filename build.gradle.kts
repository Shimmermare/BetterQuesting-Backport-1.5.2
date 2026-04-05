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

//val shadowConfig by configurations.creating

dependencies {
    minecraft(libs.minecraft)
    forge(variantOf(libs.forge) { classifier("universal"); artifactType("zip") })
    mappings(variantOf(libs.forge) { classifier("src"); artifactType("zip") })

    compileOnly(libs.jsr305)
    //shadowConfig("com.google.code.gson:gson:2.8.9")
    //compileOnly(shadowConfig)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(jdkVersion))
    }
}

tasks {
    compileJava {
        options.release.set(compileTargetVersion)
    }
    compileTestJava {
        options.release.set(jdkVersion)
    }
    test {
        useJUnitPlatform()
    }
    jar {
        archiveBaseName.set(modBaseName)
        // TODO: Shadow package too
        //from(shadowConfig.map { if (it.isDirectory) it else zipTree(it) }) {
        //    exclude("module-info.class")
        //    exclude("META-INF/MANIFEST.MF")
        //}
    }
}

volde {
    runs {
        getByName<RunConfig>("client") {
            programArg("Player")
        }
    }
}

