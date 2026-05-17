# Build stage
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /build

# Copy gradle wrapper and config
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src src

# Copy frontend files into static resources
RUN mkdir -p src/main/resources/static
COPY frontend/* src/main/resources/static/

# Build the application
RUN ./gradlew clean bootJar --no-daemon

# List what was built
RUN echo "=== Built JAR files ===" && ls -lh /build/build/libs/

# Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the JAR from builder stage
COPY --from=builder /build/build/libs/*.jar app.jar

# Copy startup script
COPY start.sh .
RUN chmod +x start.sh

# Verify the JAR exists
RUN ls -lh /app/

# Expose port
EXPOSE 8080

# Set environment defaults
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SERVER_PORT=8080

# Run the application
CMD ["./start.sh"]
