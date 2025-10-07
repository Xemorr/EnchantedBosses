import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version("8.3.6")
    id("io.sentry.jvm.gradle") version("3.12.0")
}

group = "me.xemor"
version = "2.0.1"
description = "Enchanted Bosses"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo.minebench.de/") }
    maven { url = uri("https://repo.xemor.zip/releases")}
    maven { url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")}
    maven { url = uri("https://mvn-repo.arim.space/lesser-gpl3/") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.5-R0.1-SNAPSHOT")
    compileOnly("com.fasterxml.jackson.core:jackson-core:2.18.0")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    compileOnly("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.7.0")
    compileOnly("me.xemor:skillslibrary:4.1.1")
    shadow("org.jetbrains:annotations:23.0.0")
    shadow("me.xemor:configurationdata:4.3.15")
    shadow("space.arim.morepaperlib:morepaperlib:0.4.3")
    shadow("me.xemor:foliahacks:1.7.4")
    compileOnly("me.xemor:skillslibrary:2.19.1")
    shadow("net.kyori:adventure-platform-bukkit:4.3.4")
    shadow("net.kyori:adventure-text-minimessage:4.17.0")
    shadow("org.bstats:bstats-bukkit:1.7")
}

java {
    configurations.shadow.get().dependencies.remove(dependencies.gradleApi())
}

var free = tasks.register<ShadowJar>("free") {
    archiveFileName.set("enchanted-bosses-free-$version.jar")
    from(sourceSets.main.get().output)
}

free {
    minimize()
    relocate("org.jetbrains", "me.xemor.enchantedbosses.org.jetbrains")
    relocate("me.xemor.configurationdata", "me.xemor.enchantedbosses.configurationdata")
    relocate("net.kyori", "me.xemor.enchantedbosses.kyori")
    relocate("org.bstats", "me.xemor.enchantedbosses.bstats")
    relocate("space.arim.morepaperlib", "me.xemor.enchantedbosses.morepaperlib")
    relocate("me.xemor.foliahacks", "me.xemor.enchantedbosses.foliahacks")
    configurations = listOf(project.configurations.shadow.get())
    val folder = System.getenv("pluginFolder")
    destinationDirectory.set(file(folder))
}

var premiumSourceSets: FileCollection = sourceSets.main.get().output
premiumSourceSets = premiumSourceSets.plus(project.files(project.file("premium-checksum.txt")))

var premium = tasks.register<ShadowJar>("premium") {
    archiveFileName.set("enchanted-bosses-$version.jar")
    from(premiumSourceSets)
}

premium {
    minimize()
    relocate("org.jetbrains", "me.xemor.enchantedbosses.org.jetbrains")
    relocate("me.xemor.configurationdata", "me.xemor.enchantedbosses.configurationdata")
    relocate("net.kyori", "me.xemor.enchantedbosses.kyori")
    relocate("org.bstats", "me.xemor.enchantedbosses.bstats")
    configurations = listOf(project.configurations.shadow.get())
    val folder = System.getenv("pluginFolder")
    destinationDirectory.set(file(folder))
}


tasks.processResources {
    expand(project.properties)
}