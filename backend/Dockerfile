# Fase 1: Compilación (Maven + Java 21)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos el archivo de dependencias y el código fuente
COPY pom.xml .
COPY src ./src
# Compilamos el proyecto saltando los tests para que sea más rápido
RUN mvn clean package -DskipTests

# Fase 2: Ejecución (Solo Java 21, imagen mucho más ligera)
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copiamos ÚNICAMENTE el .jar generado de la Fase 1
# Usamos *.jar por si la versión en el pom.xml cambia en el futuro
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
