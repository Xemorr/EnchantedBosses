import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `kotlin-dsl`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version("7.1.2")
}

group = "me.xemor"
version = "1.3.2"

repositories {
    mavenCentral()
    mavenLocal()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://repo.minebench.de/") }
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/") {
        name = "sonatype-oss-snapshots"
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.5-R0.1-SNAPSHOT")
    shadow("org.jetbrains:annotations:23.0.0")
    shadow("me.xemor:configurationdata:3.4.2-SNAPSHOT")
    compileOnly("me.xemor:skillslibrary:2.19.1")
    shadow("net.kyori:adventure-platform-bukkit:4.3.3-SNAPSHOT")
    shadow("org.bstats:bstats-bukkit:1.7")
}

java {
    configurations.shadow.get().dependencies.remove(dependencies.gradleApi())
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
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