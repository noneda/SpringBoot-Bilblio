FROM gradle:8.10-jdk21-alpine AS build

WORKDIR /app-build

COPY settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./
COPY gradlew.bat ./

COPY app/build.gradle.kts ./app/

RUN ./gradlew dependencies --no-daemon || true

COPY app/src ./app/src

RUN ./gradlew :app:clean :app:build --no-daemon

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /app-build/app/build/libs/app.jar .

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
