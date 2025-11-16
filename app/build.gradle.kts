plugins {
    application
    kotlin("jvm") version "2.0.21"
}

application {
    mainClass.set("org.bibliodigit.App")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.hibernate.core)
    implementation(libs.postgresql)
    implementation(libs.slf4j.api)
    implementation(libs.logback.classic)
    implementation(libs.jakarta.persistence.api)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter)
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    from(
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    )

    archiveFileName.set("app-all.jar")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
