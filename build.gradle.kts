plugins {
    java
    base
    idea
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

repositories {
    mavenCentral()
}

dependencies {
}

// Define repositories for buildscript
buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
    }
    dependencies {
        classpath("io.spring.gradle:dependency-management-plugin:1.1.7")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:3.4.5")
        classpath("org.gretty:gretty:4.0.3")
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}

val aggregatorModules = listOf("basic", "advanced")
subprojects {
    if (name !in aggregatorModules) {
        apply(plugin = "java")
        apply(plugin = "org.springframework.boot")
        apply(plugin = "io.spring.dependency-management")

        repositories {
            mavenCentral()
        }

        dependencies {
            implementation("org.springframework.boot:spring-boot-starter")
            implementation("org.springframework.boot:spring-boot-starter-integration")
            testImplementation("org.springframework.boot:spring-boot-starter-test")

            testImplementation("org.springframework.integration:spring-integration-test")
            testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        }

        java {
            toolchain.languageVersion.set(JavaLanguageVersion.of(24))
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}