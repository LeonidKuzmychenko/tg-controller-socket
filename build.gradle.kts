plugins {
    java
    id("org.springframework.boot") version "3.1.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "lk.tech"
version = "0.0.1-SNAPSHOT"
description = "tg-controller-socket"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
//    implementation("org.springframework:spring-webflux")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.12")
//    implementation("org.springframework.boot:spring-boot-starter-websocket")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
