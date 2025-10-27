plugins {
    id("multiloader-common")
    alias(libs.plugins.moddev)
}

neoForge {
    neoFormVersion = libs.versions.neoForm // Automatically enable AccessTransformers if the file exists
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = libs.versions.parchmentMC
        mappingsVersion = libs.versions.parchment
    }
}

dependencies {
    compileOnly(libs.mixin)

    compileOnly("com.terraformersmc:modmenu:7.2.2")
    compileOnly("dev.emi:emi-fabric:1.1.22+1.20.1:api")

    compileOnly("dev.emi:emi-forge:1.1.22+1.20.1:api")
    compileOnly("org.appliedenergistics:guideme:20.1.14:api")

    compileOnly("com.github.jnr:jnr-ffi:2.2.13")
}

repositories {
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonKotlin") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonKotlin", sourceSets.main.get().kotlin.sourceDirectories.filter { !it.name.endsWith("java") }.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}
