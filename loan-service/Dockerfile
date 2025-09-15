# ==========================
#  Etapa 1: Build con Gradle
# ==========================
FROM gradle:8.10.0-jdk17-alpine AS build
WORKDIR /app

# --- Copiar wrapper y asegurar permisos ---
COPY gradlew .
COPY gradle ./gradle
RUN chmod +x gradlew

# --- Copiar archivos de configuración ---
COPY build.gradle settings.gradle ./

# --- Descargar dependencias (mejora cacheo) ---
RUN ./gradlew dependencies --no-daemon || return 0

# --- Copiar el resto del código fuente ---
COPY . .

# --- Compilar y generar jar ---
RUN ./gradlew clean bootJar --no-daemon

# ==========================
#  Etapa 2: Imagen final
# ==========================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiar el jar generado desde la etapa de build
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer puerto (ajústalo si tu micro cambia)
EXPOSE 8080

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]
