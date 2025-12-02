# ===========================
# 1) BUILD STAGE
# ===========================
FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /workspace/app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src

RUN chmod +x gradlew

RUN ./gradlew clean nativeCompile -PskipTests


# ===========================
# 2) RUNTIME STAGE (Debian)
# ===========================
FROM debian:bookworm-slim

RUN apt-get update && apt-get install -y libstdc++6 && apt-get clean

WORKDIR /app

COPY --from=builder /workspace/app/build/native/nativeCompile/tg-controller-socket /app/tg-controller-socket

RUN chmod +x /app/tg-controller-socket

EXPOSE 8484

ENTRYPOINT ["/app/tg-controller-socket"]
