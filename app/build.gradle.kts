plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)  // ← AGREGAR
    java
    application
}

group = "org.bibliodigit"
version = "1.0.2"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation(libs.starter.web)
    implementation(libs.starter.data.jpa)
    implementation(libs.starter.validation)

    // Base de Datos
    runtimeOnly(libs.postgresql)
    testRuntimeOnly(libs.h2)
    
    // Lombok
    compileOnly(libs.java.lombok)
    annotationProcessor(libs.java.lombok)
    testCompileOnly(libs.java.lombok)
    testAnnotationProcessor(libs.java.lombok)

    // Testing
    testImplementation(libs.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

application {
    mainClass.set("org.bibliodigit.App")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName.set("app.jar")
}
