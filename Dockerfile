
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

# Dar permisos de ejecución
RUN chmod +x ./gradlew

# Descargar dependencias (cacheado)
RUN ./gradlew :app:dependencies --no-daemon

# --- Stage 2: Test Stage (NUEVO) ---
FROM build AS test
WORKDIR /app-build

# Ejecutar solo los tests
RUN ./gradlew :app:test --no-daemon


# --- Stage 3: Build JAR ---
FROM build AS jar-builder
WORKDIR /app-build

# Construir el JAR (sin ejecutar tests)
RUN ./gradlew :app:clean :app:build -x test --no-daemon

# --- Stage 4: Runtime ---
FROM amazoncorretto:21-alpine AS runtime
WORKDIR /app

COPY --from=jar-builder /app-build/app/build/libs/app.jar .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
