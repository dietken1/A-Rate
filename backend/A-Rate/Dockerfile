FROM openjdk:17-jdk-slim

WORKDIR /app

COPY ./gradlew .
COPY ./gradlew.bat .
COPY ./settings.gradle .
COPY ./build.gradle .
COPY ./gradle ./gradle

RUN chmod +x ./gradlew
RUN ./gradlew dependencies

EXPOSE 8080

CMD ["./gradlew", "bootRun"] 