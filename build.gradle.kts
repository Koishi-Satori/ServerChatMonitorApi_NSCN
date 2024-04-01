import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.libsDirectory

plugins {
    kotlin("jvm") version "1.9.21"
}

group = "top.kkoishi"
version = "1.0-alpha"
val nsModPackageName = "Satori_KKoishi-ServerChatMonitor-NS"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("io.netty", "netty-all", "4.1.10.Final")
    implementation("com.google.code.gson:gson:2.10.1")
    api("net.mamoe", "mirai-core", "2.16.0")
}

tasks.test {
    useJUnitPlatform()
}
tasks.compileKotlin {

}
// package jar file
tasks.jar {
    archiveFileName.set("top.kkoishi.ns.scmonitior-api.jar")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest.attributes["Main-Class"] = "top.kkoishi.scmonitor.MainKt"
    from(configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) })
}
// package rSquirrel
task("northstar.mod.rSquirrel") {
    val libsDir = libsDirectory.get().asFile.path
    projectDir.listFiles { f: File -> f.isDirectory && f.name == "sq_mod" }
        ?.first()
        ?.run {
            // create package dir
            val modDir = File("$libsDir/$nsModPackageName")
            modDir.mkdirs()
            // copy contents
            this.copyRecursively(modDir, true)
        }
}
//kotlin {
//    jvmToolchain(17)
//}