plugins {
    java
    idea
    application
}

group = "ninja.cardcarrying"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("ninja.cardcarrying.Graphics")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    // https://mvnrepository.com/artifact/org.jogamp.jogl/jogl
    implementation("org.jogamp.jogl:jogl-all-main:2.6.0")
    // https://mvnrepository.com/artifact/org.jogamp.gluegen/gluegen-rt-main
    implementation("org.jogamp.gluegen:gluegen-rt-main:2.6.0")
    // https://mvnrepository.com/artifact/org.joml/joml
    implementation("org.joml:joml:1.10.8+")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}