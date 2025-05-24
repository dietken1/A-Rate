FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 디버깅을 위한 도구 설치
RUN apk add --no-cache bash curl

# Gradle 래퍼와 소스 코드 복사
COPY . .

# Gradle 빌드 실행 (상세 로그 출력)
RUN chmod +x ./gradlew && \
    ./gradlew clean && \
    ./gradlew bootJar --no-daemon --info --stacktrace

# 빌드 결과 확인
RUN echo "=== Build directory contents ===" && \
    ls -la build/ && \
    echo "=== Libs directory contents ===" && \
    ls -la build/libs/

# 빌드된 JAR 파일 실행
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "\
    echo '=== Java Version ===' && \
    java -version && \
    echo '=== JAR Contents ===' && \
    jar tf build/libs/*.jar && \
    echo '=== Main Class Manifest ===' && \
    unzip -p build/libs/*.jar META-INF/MANIFEST.MF && \
    echo '=== Starting Application ===' && \
    java -jar build/libs/*.jar"]