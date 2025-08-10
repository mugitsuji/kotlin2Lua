plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "com.mugitsuji"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

application {
    mainClass.set("com.mugitsuji.kotlin2lua.MainKt")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}