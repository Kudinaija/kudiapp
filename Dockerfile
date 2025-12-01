# Use Eclipse Temurin JDK 21 as base image for build stage
FROM eclipse-temurin:21-jdk-alpine AS build

# Set working directory
WORKDIR /app

# Copy Maven wrapper and dependencies
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw ./

# Make mvnw executable
RUN chmod +x ./mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create a new stage for the runtime
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar /app/kudiapp.jar

# Install curl for health check
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1001 -S spring && adduser -u 1001 -S spring -G spring
USER spring:spring

# Expose the port your app runs on
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=300s --retries=5 \
  CMD curl --fail http://localhost:2026/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/kudiapp.jar"]
