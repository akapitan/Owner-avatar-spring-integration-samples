plugins {
    java
    id("org.springframework.boot") version "3.4.5"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.integration:spring-integration-amqp")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.integration:spring-integration-http")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.integration:spring-integration-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val aggregatorModules = listOf("basic", "advanced")
subprojects {
    if (name !in aggregatorModules) {
        apply(plugin = "java")
        apply(plugin = "io.spring.dependency-management")
        apply(plugin = "org.springframework.boot")

        repositories {
            mavenCentral()
        }
        dependencyManagement {
            imports {
                mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.5")
            }
        }

        dependencies {
            implementation("org.springframework.boot:spring-boot-starter")
            testImplementation("org.springframework.boot:spring-boot-starter-test")
        }

        java {
            toolchain.languageVersion.set(JavaLanguageVersion.of(24))
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}