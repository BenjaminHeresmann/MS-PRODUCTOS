# Multi-stage build para Railway
# Etapa 1: Build
FROM maven:3.8.5-openjdk-17-slim AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Copiar el wallet de Oracle (si es necesario)
COPY Wallet_BDFULLSTACK/ /app/Wallet_BDFULLSTACK/

# Exponer el puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
