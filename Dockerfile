# Multi-stage build for optimal image size
FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven files first to leverage Docker cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# Final stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/zest-0.0.1-SNAPSHOT.jar app.jar

# Expose port (Railway will set PORT environment variable)
EXPOSE 8080

# Run the application with dynamic port support
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]