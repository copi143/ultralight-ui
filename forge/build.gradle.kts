import org.gradle.internal.extensions.stdlib.capitalized

plugins {
    id("multiloader-loader")
    alias(libs.plugins.moddev)
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val modId: String by project

mixin {
    add(sourceSets.main.get(), "${modId}.refmap.json")
    config("${modId}.mixins.json")
    config("${modId}.forge.mixins.json")
}

tasks.jar {
    dependsOn(tasks.shadowJar)
    from(zipTree(tasks.shadowJar.get().archiveFile))
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["MixinConfigs"] = "${modId}.mixins.json,${modId}.forge.mixins.json"
    }
}

tasks.shadowJar {
    archiveClassifier.set("shadow")
    configurations = listOf(project.configurations.getByName("shadow"))
    from(sourceSets["main"].output)

    // 注意：relocate 会爆炸

    exclude("META-INF/maven/**")
    exclude("org/objectweb/**")
}

tasks.reobfJar {
    dependsOn(tasks.jar)
}

tasks.build {
    dependsOn(tasks.reobfJar)
}

neoForge {
    version = libs.versions.forge // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = libs.versions.parchmentMC
        mappingsVersion = libs.versions.parchment
    }
    runs {
        configureEach {
            systemProperty("forge.enabledGameTestNamespaces", modId)
            ideName = "Forge ${name.capitalized()} (${project.path})" // Unify the run config names with fabric
        }
        register("client") {
            client()
        }
        register("data") {
            data()
        }
        register("server") {
            server()
        }
    }
    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources { srcDir("src/generated/resources") }

dependencies {
    modImplementation(libs.kff)
    annotationProcessor(variantOf(libs.mixin) { classifier("processor") })

    // 完蛋啦，这回 mixin 注入也爆炸了
    compileOnly("dev.emi:emi-forge:1.1.22+1.20.1:api")
    runtimeOnly("dev.emi:emi-forge:1.1.22+1.20.1")

    // 救不了了，让它炸吧
    compileOnly("org.appliedenergistics:guideme:20.1.14:api")
    runtimeOnly("org.appliedenergistics:guideme:20.1.14")

    // 不知道为什么找不到类，但是构建后外部运行可以
    implementation("com.github.jnr:jnr-ffi:2.2.17")

    shadow("com.github.jnr:jnr-ffi:2.2.17")
}

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}
