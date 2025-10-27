plugins {
    id("multiloader-loader")
    alias(libs.plugins.loom)
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

val modId: String by project

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${libs.versions.parchmentMC.get()}:${libs.versions.parchment.get()}@zip")
    })
    modImplementation(libs.fabricLoader)
    modImplementation(libs.fabricApi)

    modImplementation(libs.flk)

    modImplementation("com.terraformersmc:modmenu:7.2.2")
    modCompileOnly("dev.emi:emi-fabric:1.1.22+1.20.1:api")
    modLocalRuntime("dev.emi:emi-fabric:1.1.22+1.20.1")

    implementation("com.github.jnr:jnr-ffi:2.2.17")

    shadow("com.github.jnr:jnr-ffi:2.2.17")
}

tasks.shadowJar {
    archiveClassifier.set("shadow")
    configurations = listOf(project.configurations.getByName("shadow"))
    from(sourceSets["main"].output)

    // 注意：relocate 会爆炸

    exclude("META-INF/maven/**")
    exclude("org/objectweb/**")
}

tasks.remapJar {
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.get().archiveFile)
}

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

loom {
    val aw = project(":common").file("src/main/resources/${modId}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("runs/client")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}
