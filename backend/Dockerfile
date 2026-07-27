# Stage 1: Build
# Using Alpine to reduce image size and OS vulnerabilities
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# PERFORMANCE TRICK: Copy only the pom.xml first.
# This allows Docker to cache dependencies. If you only change the source code,
# Maven won't need to redownload all dependencies.
COPY pom.xml .
RUN mvn dependency:go-offline

# Now copy the source code and build the project
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Secure Execution
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# SECURITY: Create an unprivileged user to avoid running the app as Root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy the compiled JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
