# Spring Boot 프로젝트를 Docker 이미지로 만드는 파일
# Docker 이미지 빌드 과정에서 Gradle 빌드까지 같이 수행

# 1단계: Gradle로 jar 빌드
FROM gradle:8.7-jdk17 AS builder

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./
COPY src ./src

RUN chmod +x ./gradlew
RUN ./gradlew clean bootJar -x test

# 2단계: jar 실행
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]