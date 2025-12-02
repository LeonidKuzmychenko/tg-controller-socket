plugins {
    java
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.graalvm.buildtools.native") version "0.11.3"
}

group = "lk.tech"
version = "0.0.1-SNAPSHOT"
description = "tg-controller-socket"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
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
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

graalvmNative {
    toolchainDetection.set(true)

    binaries {
        named("main") {
            imageName.set("tg-controller-socket")
            fallback.set(false)

            // ===== GC (только serial или epsilon) =====
            buildArgs.add("--gc=serial")

            // ===== Разрешённые протоколы =====
            buildArgs.add("--enable-http")
            buildArgs.add("--enable-https")
            buildArgs.add("--enable-url-protocols=http,https")

            // ===== Netty — строго initialize-at-run-time =====
            listOf(
                "io.netty",
//                "io.netty.buffer",
//                "io.netty.channel",
//                "io.netty.handler",
//                "io.netty.resolver",
//                "io.netty.resolver.dns",
//                "io.netty.transport",
//                "io.netty.util",
//                "io.netty.util.internal",
//                "org.springframework.core.io.buffer"
            ).forEach { pkg ->
                buildArgs.add("--initialize-at-run-time=$pkg")
            }

            // ===== Security / Crypto =====
            buildArgs.add("--enable-all-security-services")

            // ===== Native access =====
            buildArgs.add("--enable-native-access=ALL-UNNAMED")

            // ===== Логирование при сборке =====
            buildArgs.add("--verbose")
        }
    }
}