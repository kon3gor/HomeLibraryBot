plugins {
    kotlin("jvm") version "1.3.72"
}

group = "org.eshendo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven( url = "https://jitpack.io" )
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.4")
    implementation("com.google.firebase:firebase-admin:7.1.1")
    implementation("me.xdrop:fuzzywuzzy:1.3.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}