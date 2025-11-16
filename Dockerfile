# --- Stage 1: Build Stage ---
FROM gradle:9.2.0-jdk21 AS build

WORKDIR /app-build

# Copiar wrapper y configuración del root
COPY gradlew gradlew.bat settings.gradle.kts gradle.properties /app-build/
COPY gradle /app-build/gradle

# Copiar el build.gradle.kts del módulo app
COPY app/build.gradle.kts /app-build/app/build.gradle.kts

# Copiar el código fuente del módulo app
COPY app/src /app-build/app/src

# Construcción
RUN chmod +x ./gradlew
RUN ./gradlew :app:clean :app:build -x test

# --- Stage 2: Runtime ---
FROM amazoncorretto:21-alpine

WORKDIR /app

COPY --from=build /app-build/app/build/libs/app-all.jar .

ENTRYPOINT ["java", "-jar", "app-all.jar"]
