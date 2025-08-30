# Stage 1: Build the application using Maven
FROM maven:3.9.9-amazoncorretto-17 AS builder
WORKDIR /build
# Copy pom.xml and download dependencies (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests  # Skip tests for faster builds; remove if needed

# Stage 2: Create the runtime image
FROM amazoncorretto:17-alpine-jdk
WORKDIR /app
# Create logs directory and set permissions
RUN mkdir -p /app/logs && chmod -R 777 /app/logs
# Copy the JAR from the build stage (use wildcard to handle dynamic version)
COPY --from=builder /build/target/*.jar app.jar

# Run as non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
