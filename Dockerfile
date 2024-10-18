# Establecer la imagen base de Java
FROM gradle:7.6.0-jdk17 AS build

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar archivos de Gradle y configuración
COPY settings.gradle gradlew gradlew.bat ./
COPY gradle gradle

# Copiar el resto de los archivos del proyecto
COPY . .

# Construir la aplicación
RUN ./gradlew build --no-daemon

# Usar una imagen base de OpenJDK para ejecutar la aplicación
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo JAR desde la etapa de construcción
COPY --from=build /app/build/libs/*.jar app.jar

# Exponer el puerto en el que la aplicación escuchará
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]

