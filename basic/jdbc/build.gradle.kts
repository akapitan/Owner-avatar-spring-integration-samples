plugins {
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.integration:spring-integration-jdbc")
    implementation("com.h2database:h2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}