# Usar una imagen base de OpenJDK 17
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo jar de la aplicación
COPY target/prueba-0.0.1-SNAPSHOT.jar app.jar

# Copiar el wallet de Oracle (si es necesario)
COPY Wallet_BDFULLSTACK/ /app/Wallet_BDFULLSTACK/

# Exponer el puerto
EXPOSE 8089

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV PORT=8089

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
