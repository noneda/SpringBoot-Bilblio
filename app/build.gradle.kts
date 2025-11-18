plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.java)
    application
}

group = "org.bibliodigit"
version = "1.0.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))

    implementation(libs.starter.web)
    implementation(libs.starter.data.jpa)
    implementation(libs.starter.validation)  

    // PostgreSQL para producción
    runtimeOnly(libs.postgresql)
    
    // H2 solo para tests
    testRuntimeOnly(libs.h2)
    
    compileOnly(libs.java.lombok)
    annotationProcessor(libs.java.lombok)

    testImplementation(libs.starter.test)
    testAnnotationProcessor(libs.java.lombok)
    testRuntimeOnly(libs.junit.platform.launcher)
}

application {
    mainClass.set("org.bibliodigit.App")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("app.jar")
}
