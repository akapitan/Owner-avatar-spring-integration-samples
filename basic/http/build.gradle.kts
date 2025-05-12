plugins {

}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.integration:spring-integration-http")
}

springBoot {
    mainClass.set("com.example.basic.http.HttpApplication")
}