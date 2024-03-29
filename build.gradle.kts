plugins {
    id("org.springframework.boot") version "2.6.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("java")
    id("jacoco")
}

group = "online.poll"
version = "0.0.1-SNAPSHOT"

jacoco {
    toolVersion = "0.8.8"
}

repositories {
    mavenCentral()
    maven(url = "https://repo.spring.io/libs-milestone")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.8.0")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.kafka:spring-kafka")
//    implementation("org.apache.kafka:kafka-clients:3.2.0")
//    testImplementation("org.apache.kafka:kafka-clients:3.2.0")
//    testImplementation("org.scala-lang:scala-library:2.13.8")
//    testImplementation("org.apache.kafka:kafka_2.12:3.0.0")
    testImplementation("org.springframework.kafka:spring-kafka-test")
//    testImplementation("org.testcontainers:kafka:1.17.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks {
    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }
}