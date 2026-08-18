# 1단계: 빌드 환경 (Java 17)
FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app

# Gradle 관련 파일 복사 (캐시 활용을 위해 먼저 복사)
COPY gradlew .
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 소스 코드 복사 및 빌드 진행
COPY src src
RUN ./gradlew bootJar --no-daemon

# 2단계: 실행 환경 (빌드 도구 없이 가벼운 JRE만 포함)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 1단계에서 빌드된 jar 파일을 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Render가 인식할 포트 노출
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
