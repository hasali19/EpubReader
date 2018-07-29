group = "uk.co.hasali"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

plugins {
    kotlin("jvm") version "1.2.51"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
