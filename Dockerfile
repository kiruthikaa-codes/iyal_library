# Build stage
FROM gradle:8.10.2-jdk17 AS build

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Download dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src ./src
COPY frontend ./frontend

# Build the application
RUN ./gradlew bootJar --no-daemon

# List the built JAR to verify
RUN ls -la /app/build/libs/

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy all JAR files from build stage
COPY --from=build /app/build/libs/*.jar /app/

# Expose port
EXPOSE 8080

# Run the application - find the JAR dynamically
ENTRYPOINT ["sh", "-c", "java -jar /app/*.jar"]
